import os
import numpy as np
import cv2
import dnnlib
import dnnlib.tflib as tflib
from training import misc
from PIL import Image
from cp_vton import geometric_matching_module, try_on_module
import streamlit as st

# Initialize TensorFlow for StyleGAN2
tflib.init_tf()

# Paths
STYLEGAN2_WEIGHTS = 'gdrive:networks/stylegan2-clothes-config.pkl'  # Pretrained StyleGAN2 model
CP_VTON_GMM_WEIGHTS = './cp-vton/weights/gmm.pth'                  # GMM weights
CP_VTON_TOM_WEIGHTS = './cp-vton/weights/tom.pth'                  # TOM weights
DATASET_PATH = 'VirtualTryOn/Dataset/train/'                         # Dataset path

# Load StyleGAN2 Model
_, _, Gs = misc.load_pkl(STYLEGAN2_WEIGHTS)

# Helper Functions
def preprocess_image(image_path, size=(256, 256)):
    """Preprocess input images."""
    img = cv2.imread(image_path)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = cv2.resize(img, size)
    return img

def generate_latent_image(latent_vector, truncation_psi=0.7):
    """Generate an image using StyleGAN2."""
    fmt = dict(func=tflib.convert_images_to_uint8, nchw_to_nhwc=True)
    images = Gs.run(latent_vector, None, truncation_psi=truncation_psi, randomize_noise=True, output_transform=fmt)
    return Image.fromarray(images[0], 'RGB')

def run_gmm(person_image, cloth_image):
    """Run Geometric Matching Module."""
    warped_cloth = geometric_matching_module.align_cloth_with_gmm(person_image, cloth_image, CP_VTON_GMM_WEIGHTS)
    return warped_cloth

def run_tom(person_image, warped_cloth):
    """Run Try-On Module."""
    final_tryon_image = try_on_module.try_on_cloth(person_image, warped_cloth, CP_VTON_TOM_WEIGHTS)
    return final_tryon_image

# Streamlit App
st.title("StyleGAN2-Based Virtual Try-On")
st.write("Upload a person image and a clothing image to generate a virtual try-on.")

# Upload Files
uploaded_person = st.file_uploader("Upload Person Image", type=["jpg", "png"])
uploaded_cloth = st.file_uploader("Upload Clothing Image", type=["jpg", "png"])

if uploaded_person and uploaded_cloth:
    # Load images
    person_image = preprocess_image(uploaded_person)
    cloth_image = preprocess_image(uploaded_cloth)

    # StyleGAN2 Latent Vector Generation
    person_latent = np.random.randn(1, Gs.input_shape[1])
    cloth_latent = np.random.randn(1, Gs.input_shape[1])
    combined_latent = np.hstack([person_latent, cloth_latent])
    
    generated_person_image = generate_latent_image(person_latent)
    st.image(generated_person_image, caption="Generated Person Image")

    # Step 1: Geometric Matching with GMM
    st.write("Running Geometric Matching Module (GMM)...")
    warped_cloth = run_gmm(person_image, cloth_image)
    st.image(warped_cloth, caption="Warped Cloth (GMM Output)")

    # Step 2: Try-On Module
    st.write("Running Try-On Module (TOM)...")
    final_tryon_image = run_tom(person_image, warped_cloth)
    st.image(final_tryon_image, caption="Final Try-On Output")

    # Save the results
    final_output_path = './results/tryon_result.jpg'
    cv2.imwrite(final_output_path, cv2.cvtColor(final_tryon_image, cv2.COLOR_RGB2BGR))
    st.write(f"Result saved at: {final_output_path}")
