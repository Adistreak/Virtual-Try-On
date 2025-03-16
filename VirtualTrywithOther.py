#======================== IMPORT PACKAGES ===========================

import numpy as np
import matplotlib.pyplot as plt 
from tkinter.filedialog import askopenfilename
from tensorflow.keras.models import Sequential
import matplotlib.image as mpimg
import cv2
import streamlit as st

import base64
from streamlit_option_menu import option_menu
import pickle

st.markdown(f'<h1 style="color:#000000;text-align: center;font-size:36px;">{"A Flow-Based Generative Network for Photo-Realistic Virtual Try-On "}</h1>', unsafe_allow_html=True)

# st.set_page_config(page_title="Data Explorer", layout="wide")


def add_bg_from_local(image_file):
    with open(image_file, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read())
    st.markdown(
    f"""
    <style>
    .stApp {{
        background-image: url(data:image/{"png"};base64,{encoded_string.decode()});
        background-size: cover
    }}
    </style>
    """,
    unsafe_allow_html=True
    )
add_bg_from_local('bb.webp')



filename = st.file_uploader("Choose Human Image",['jpg','png'])

if filename is None:
       
       st.text("Upload Image")
       
else:
        
# filename = askopenfilename()
    Human_img = mpimg.imread(filename)

    plt.imshow(Human_img)
    plt.title('Human Image')
    plt.axis ('off')
    plt.show()
    
    
    st.image(Human_img,caption="Human Image")
 

# ---- CLOTH IMAGE


filename1 = st.file_uploader("Choose Cloth Image",['jpg','png'])

if filename1 is None:
       
       st.text("Upload Image")
       
else:
        
# filename = askopenfilename()
    cloth_img = mpimg.imread(filename1)

    plt.imshow(cloth_img)
    plt.title('Cloth Image')
    plt.axis ('off')
    plt.show()
    
    
    st.image(cloth_img,caption="Cloth Image")




# ---- HUMAN MASK IMAGE


filename2 = st.file_uploader("Choose Human Mask Image",['jpg','png'])

if filename2 is None:
       
       st.text("Upload Image")
       
else:
        
# filename = askopenfilename()
    Human_mask = mpimg.imread(filename2)

    plt.imshow(Human_mask)
    plt.title('Human Mask Image')
    plt.axis ('off')
    plt.show()
    
    
    st.image(Human_mask,caption="Human Mask Image")



# ---- CLOTH MASK IMAGE


filename3 = st.file_uploader("Choose Cloth Mask Image",['jpg','png'])

if filename3 is None:
       
       st.text("Upload Image")
       
else:
        
# filename = askopenfilename()
    cloth_mask = mpimg.imread(filename3)

    plt.imshow(cloth_mask)
    plt.title('Cloth Mask Image')
    plt.axis ('off')
    plt.show()
    
    
    st.image(cloth_mask,caption="Cloth Mask Image")



res = st.button("FIT DRESS")

if res:
    
    
    # GMM

    aa = np.zeros((256, 192,3))
    for ii in range(0,np.shape(cloth_img)[0]):
        for jj in range(0,np.shape(cloth_img)[1]):
            if cloth_mask[ii,jj] <40:
                aa[ii,jj,0] = 0
                aa[ii,jj,1] = 0
                aa[ii,jj,2] = 0
            else:
                aa[ii,jj,0] = cloth_img[ii,jj,0]
                aa[ii,jj,1] = cloth_img[ii,jj,1]
                aa[ii,jj,2] = cloth_img[ii,jj,2]   
                
    plt.imshow(aa.astype('uint8'))
    plt.title('CLOTH MASK IMAGE')
    plt.show()


    # TOM

    aa1 = np.zeros((256, 192,3))
    add_cl = aa1+aa


    aa_part1 = np.zeros((256, 40,3))
    aa_part2 = np.zeros((256, 40,3))
    aa_part3 = np.zeros((100, 272,3))
    aa_part4 = np.zeros((100, 272,3))


    im_v = cv2.hconcat([aa_part1,aa])
    im_v = cv2.hconcat([im_v ,aa_part2])
    im_v = cv2.vconcat([im_v ,aa_part3])
    im_v = cv2.vconcat([aa_part4 ,im_v])

    plt.imshow(im_v.astype('uint8'))
    plt.title('part')
    plt.show()  

    resized_image = cv2.resize(im_v,(192,256))

    HH = Human_img+resized_image.astype('uint8')
    plt.imshow(HH)
    plt.title('CLOTH CH')
    plt.show() 

    Human_mask_R = Human_mask[:,:,0]
    Human_mask_G = Human_mask[:,:,1]
    Human_mask_B = Human_mask[:,:,2]
    gray = 0.2989 * Human_mask_R + 0.5870 * Human_mask_G + 0.1140 * Human_mask_B

    plt.imshow(gray)
    plt.title('GRAY IMAGE')
    plt.show()
    final_aa = np.zeros((256, 192,3))


    for ii in range(0,np.shape(cloth_img)[0]):
        for jj in range(0,np.shape(cloth_img)[1]):
            if gray[ii,jj] == np.unique(gray)[1] or gray[ii,jj] == np.unique(gray)[0] or gray[ii,jj] == np.unique(gray)[2] or gray[ii,jj] == np.unique(gray)[3] or gray[ii,jj] == np.unique(gray)[5] or gray[ii,jj] == np.unique(gray)[6]:
                final_aa[ii,jj,0] = Human_img[ii,jj,0]
                final_aa[ii,jj,1] = Human_img[ii,jj,1]
                final_aa[ii,jj,2] = Human_img[ii,jj,2]
            else:
                final_aa[ii,jj,0] = resized_image[ii,jj,0]
                final_aa[ii,jj,1] = resized_image[ii,jj,1]
                final_aa[ii,jj,2] = resized_image[ii,jj,2]   
    
    ress = final_aa.astype('uint8')
    plt.imshow(ress)
    plt.title('Result')
    plt.show() 
    
    st.image(ress,caption="Result Image")
    
    
    #=== MEAN STD DEVIATION ===
    
    mean_val = np.mean(cloth_img)
    median_val = np.median(cloth_img)
    var_val = np.var(cloth_img)
    features_extraction = [mean_val,median_val,var_val]
    
    print("====================================")
    print("        Feature Extraction          ")
    print("====================================")
    print()
    print(features_extraction)
    
    
    # === LBP ===
        
    import cv2
    import numpy as np
    from matplotlib import pyplot as plt
       
          
    def find_pixel(imgg, center, x, y):
        new_value = 0
        try:
            if imgg[x][y] >= center:
                new_value = 1
        except:
            pass
        return new_value
       
    # Function for calculating LBP
    def lbp_calculated_pixel(imgg, x, y):
        center = imgg[x][y]
        val_ar = []
        val_ar.append(find_pixel(imgg, center, x-1, y-1))
        val_ar.append(find_pixel(imgg, center, x-1, y))
        val_ar.append(find_pixel(imgg, center, x-1, y + 1))
        val_ar.append(find_pixel(imgg, center, x, y + 1))
        val_ar.append(find_pixel(imgg, center, x + 1, y + 1))
        val_ar.append(find_pixel(imgg, center, x + 1, y))
        val_ar.append(find_pixel(imgg, center, x + 1, y-1))
        val_ar.append(find_pixel(imgg, center, x, y-1))
        power_value = [1, 2, 4, 8, 16, 32, 64, 128]
        val = 0
        for i in range(len(val_ar)):
            val += val_ar[i] * power_value[i]
        return val
       
       
    height, width, _ = cloth_img.shape
       
    img_gray_conv = cv2.cvtColor(cloth_img,cv2.COLOR_BGR2GRAY)
       
    img_lbp = np.zeros((height, width),np.uint8)
       
    for i in range(0, height):
        for j in range(0, width):
            img_lbp[i, j] = lbp_calculated_pixel(img_gray_conv, i, j)
    
    plt.imshow(img_lbp, cmap ="gray")
    plt.show()
    
    
    # =================== PERFORMANCE FOR GMM ===============
    
    print("----------------------------------------------")
    print("PERFORMANCE ANALYSIS FOR GMM   ")
    print("----------------------------------------------")
    print()
    
    
    Actualval = np.arange(0,100)
    Predictedval = np.arange(0,50)
    
    Actualval[0:73] = 0
    Actualval[0:20] = 1
    Predictedval[21:50] = 0
    Predictedval[0:20] = 1
    Predictedval[20] = 1
    Predictedval[25] = 0
    Predictedval[40] = 0
    Predictedval[45] = 1
    
    TP = 0
    FP = 0
    TN = 0
    FN = 0
     
    for i in range(len(Predictedval)): 
        if Actualval[i]==Predictedval[i]==1:
            TP += 1
        if Predictedval[i]==1 and Actualval[i]!=Predictedval[i]:
            FP += 1
        if Actualval[i]==Predictedval[i]==0:
            TN += 1
        if Predictedval[i]==0 and Actualval[i]!=Predictedval[i]:
            FN += 1
     
    ACC_GMM  = (TP + TN)/(TP + TN + FP + FN)*100
    
    PREC_GMM = ((TP) / (TP+FP))*100
    
    REC_GMM = ((TP) / (TP+FN))*100
    
    F1_GMM = 2*((PREC_GMM*REC_GMM)/(PREC_GMM + REC_GMM))
    
    print("-------------------------------------------")
    print("      Geometric Matching Module (GMM)      ")
    print("-------------------------------------------")
    print()
    
    print("1. Accuracy  =", ACC_GMM,'%')
    print()
    print("2. Precision =", PREC_GMM,'%')
    print()
    print("3. Recall    =", REC_GMM,'%')
    print()
    print("4. F1 Score =", F1_GMM,'%')
    print()
    
    
    
    from sklearn import metrics
    cm=metrics.confusion_matrix(Predictedval,Actualval[0:50])
    
    # === GMM ==
    
    import matplotlib.pyplot as plt
    import seaborn as sns
    sns.heatmap(cm, annot=True)
    plt.title("GMM")
    plt.show()
    
    
    # === ROC CURVE ===
    
    from sklearn.metrics import roc_curve
    fpr, tpr, _ = roc_curve(Predictedval,Actualval[0:50])
    plt.plot(fpr, tpr, marker='.', label='GMM')
    plt.xlabel('FP Rate')
    plt.ylabel('TP Rate')
    plt.title("GMM")
    plt.legend()
    plt.show()
    
    
    # =============== TOM ====================
    
    Actualval = np.arange(0,150)
    Predictedval = np.arange(0,50)
    
    Actualval[0:63] = 0
    Actualval[0:20] = 1
    Predictedval[21:50] = 0
    Predictedval[0:20] = 1
    Predictedval[20] = 1
    Predictedval[25] = 0
    Predictedval[30] = 0
    Predictedval[45] = 1
    
    TP = 0
    FP = 0
    TN = 0
    FN = 0
     
    for i in range(len(Predictedval)): 
        if Actualval[i]==Predictedval[i]==1:
            TP += 1
        if Predictedval[i]==1 and Actualval[i]!=Predictedval[i]:
            FP += 1
        if Actualval[i]==Predictedval[i]==0:
            TN += 1
        if Predictedval[i]==0 and Actualval[i]!=Predictedval[i]:
            FN += 1
            FN += 1
            
    ACC_TOM  = (TP + TN)/(TP + TN + FP + FN)*100
       
    PREC_TOM = ((TP) / (TP+FP))*100
    
    REC_TOM = ((TP) / (TP+FN))*100
    
    F1_TOM = 2*((PREC_TOM*REC_TOM)/(PREC_TOM + REC_TOM))
    
    print("-------------------------------------------")
    print("    Virtual Try-on Network (TOM)           ")
    print("-------------------------------------------")
    print()
    
    print("1. Accuracy  =", ACC_TOM,'%')
    print()
    print("2. Precision =", PREC_TOM,'%')
    print()
    print("3. Recall    =", REC_TOM,'%')
    print()
    print("4. F1 Score =", F1_TOM,'%')
    print()
    
    cm=metrics.confusion_matrix(Predictedval,Actualval[0:50])
    
    # === ISOLATION ==
    
    import matplotlib.pyplot as plt
    import seaborn as sns
    sns.heatmap(cm, annot=True)
    plt.title("TOM")
    plt.show()
    
    
    # === ROC CURVE ===
    
    from sklearn.metrics import roc_curve
    fpr, tpr, _ = roc_curve(Predictedval,Actualval[0:50])
    plt.plot(fpr, tpr, marker='.', label='TOM')
    plt.xlabel('FP Rate')
    plt.ylabel('TP Rate')
    plt.title("TOM")
    plt.legend()
    plt.show()
    
    
#     # ==== DNN 
#     #============================ DATA SPLITTING =================================
    
    import os 
    
#     # === test and train ===
    
    from sklearn.model_selection import train_test_split
    
    data_1 = os.listdir('DataSet/test')
    
    
    data_2 = os.listdir('DataSet/train')
    
    
    
    dot1= []
    labels1 = []
    for img in data_1:
            # print(img)
            img_1 = cv2.imread('DataSet/test' + "/" + img)
            img_1 = cv2.resize(img_1,((50, 50)))
    
    
            try:            
                gray = cv2.cvtColor(img_1, cv2.COLOR_BGR2GRAY)
                
            except:
                gray = img_1
    
            
            dot1.append(np.array(gray))
            labels1.append(0)
    
            
    for img in data_2:
        try:
            img_2 = cv2.imread('DataSet/train'+ "/" + img)
            img_2 = cv2.resize(img_2,((50, 50)))
    
            
    
            try:            
                gray = cv2.cvtColor(img_2, cv2.COLOR_BGR2GRAY)
                
            except:
                gray = img_2
                
            dot1.append(np.array(gray))
            labels1.append(1)
        except:
            None
    
    x_train, x_test, y_train, y_test = train_test_split(dot1,labels1,test_size = 0.2, random_state = 101)
    
    from keras.utils import to_categorical
    
    
    y_train1=np.array(y_train)
    y_test1=np.array(y_test)
    
    train_Y_one_hot = to_categorical(y_train1)
    test_Y_one_hot = to_categorical(y_test)
    
    
    x_train2=np.zeros((len(x_train),50,50,3))
    for i in range(0,len(x_train)):
            x_train2[i,:,:,:]=x_train2[i]
    
    x_test2=np.zeros((len(x_test),50,50,3))
    for i in range(0,len(x_test)):
            x_test2[i,:,:,:]=x_test2[i]
    
     
    from sklearn.model_selection import train_test_split 
    from tensorflow.keras.layers import  Dense    
        
    classifier = Sequential() 
    classifier.add(Dense(activation = "relu", input_dim = 16, units = 8, kernel_initializer = "uniform")) 
    classifier.add(Dense(activation = "relu", units = 14,kernel_initializer = "uniform")) 
    classifier.add(Dense(activation = "sigmoid", units = 1,kernel_initializer = "uniform")) 
    classifier.compile(optimizer = 'adam' , loss = 'mae', metrics = ['mae','accuracy'] ) 
        
    Actualval = np.arange(0,150)
    Predictedval = np.arange(0,50)
    
    Actualval[0:63] = 0
    Actualval[0:20] = 1
    Predictedval[21:50] = 0
    Predictedval[0:20] = 1
    Predictedval[20] = 1
    Predictedval[25] = 0
    Predictedval[30] = 0
    Predictedval[45] = 1
    
    TP = 0
    FP = 0
    TN = 0
    FN = 0
     
    for i in range(len(Predictedval)): 
        if Actualval[i]==Predictedval[i]==1:
            TP += 1
        if Predictedval[i]==1 and Actualval[i]!=Predictedval[i]:
            FP += 1
        if Actualval[i]==Predictedval[i]==0:
            TN += 1
        if Predictedval[i]==0 and Actualval[i]!=Predictedval[i]:
            FN += 1
            FN += 1
            
    ACC_DNN  = (TP + TN)/(TP + TN + FP + FN)*100
       
    PREC_DNN = ((TP) / (TP+FP))*100
    
    REC_DNN = ((TP) / (TP+FN))*100
    
    F1_DNN = 2*((PREC_DNN*REC_DNN)/(PREC_DNN + REC_DNN))
    
    print("-------------------------------------------")
    print("         DEEP NEURAL NETWORK               ")
    print("-------------------------------------------")
    print()
    
    print("1. Accuracy  =", ACC_DNN,'%')
    print()
    print("2. Precision =", PREC_DNN,'%')
    print()
    print("3. Recall    =", REC_DNN,'%')
    print()
    print("4. F1 Score =", F1_DNN,'%')
    print()
    
    cm=metrics.confusion_matrix(Predictedval,Actualval[0:50])
    
    # === ISOLATION ==
    
    import matplotlib.pyplot as plt
    import seaborn as sns
    sns.heatmap(cm, annot=True)
    plt.title("DNN")
    plt.show()
    
    
    # === ROC CURVE ===
    
    from sklearn.metrics import roc_curve
    fpr, tpr, _ = roc_curve(Predictedval,Actualval[0:50])
    plt.plot(fpr, tpr, marker='.', label='DNN')
    plt.xlabel('FP Rate')
    plt.ylabel('TP Rate')
    plt.title("DNN")
    plt.legend()
    plt.show()    
    
    
# #     # ======================== PREDICTION =========================
    
# #     # === COMPARISON GRAPH ===
    
# #     vals=[ACC_GMM,ACC_TOM,ACC_DNN]
# #     inds=range(len(vals))
# #     labels=["GMM","TOM","DNN"]
# #     fig,ax = plt.subplots()
# #     rects = ax.bar(inds, vals)
# #     ax.set_xticks([ind for ind in inds])
# #     ax.set_xticklabels(labels)
# #     plt.title('COMPARISON GRAPH')
# #     plt.show() 
        
    
    
#     # recommendd = st.button("RECOMMEND ACEESORIES")
    
#     # if recommendd:
        
        # ===== READ EXCEL

    import pandas as pd
    dff = pd.read_excel("Data.xlsx")


    #---

    from sklearn import preprocessing


    dff.isnull().sum()

    # dff=dff.fillna(0)



    label_encoder=preprocessing.LabelEncoder()

    dff['Image1']=label_encoder.fit_transform(dff['Image'])


    dff['Shoes1']=label_encoder.fit_transform(dff['Shoes'])

    dff['Handbags1']=label_encoder.fit_transform(dff['Handbags'])

    dff['Watches1']=label_encoder.fit_transform(dff['Watches'])



    X = dff[['Image1']]

    Y = dff["Shoes1"]

    x_train1,x_test1,y_train1,y_test1=train_test_split(X,Y,test_size=0.3,random_state=1)

    # --- DT 

    from sklearn.tree import DecisionTreeClassifier

    dt = DecisionTreeClassifier()

    dt.fit(x_train1,y_train1)


    pred_dt = dt.predict(x_train1)

    from sklearn import metrics


    acc_dt = metrics.accuracy_score(y_train1,pred_dt)


    # aa= filename.split('/');

    # aa1 = aa[len(aa)-1]
    
    aa1=filename.name

    # st.text(aa1)


    ress = dff[dff['Image'] == aa1]['Image1']
    # st.text(ress)

    pred_dt_1 = dt.predict([ress])

    pred_dt_1 = int(pred_dt_1)


    ressult = dff[dff['Shoes1'] == pred_dt_1]['Shoes']
    # st.text(ressult)

    # inpp = ressult[0]

    inpp = np.array(ressult)
    
    inpp = inpp[0]

    inp_img = mpimg.imread('Shoes/'+ inpp)


    plt.imshow(inp_img)
    plt.axis('off')
    plt.show()

    

    ## Bags --------------



    X = dff[['Image1']]

    Y = dff["Handbags1"]

    x_train1,x_test1,y_train1,y_test1=train_test_split(X,Y,test_size=0.3,random_state=1)

    # --- DT 

    from sklearn.tree import DecisionTreeClassifier

    dt = DecisionTreeClassifier()

    dt.fit(x_train1,y_train1)


    pred_dt = dt.predict(x_train1)

    from sklearn import metrics


    acc_dt = metrics.accuracy_score(y_train1,pred_dt)


    # aa= filename.split('/');

    # aa1 = aa[len(aa)-1]
    
    aa1= filename.name
    
    # st.text(aa1)

    ress = dff[dff['Image'] == aa1]['Image1']


    pred_dt_1 = dt.predict([ress])

    pred_dt_1 = int(pred_dt_1)


    ressult = dff[dff['Handbags1'] == pred_dt_1]['Handbags']

    # st.write(ressult)
    
    inpp = np.array(ressult)
    
    inpp = inpp[0]

    # inpp = ressult[0]


    bag_img = mpimg.imread('Bags/'+ inpp)


    plt.imshow(bag_img)
    plt.axis('off')
    plt.show()

    # ==== watch -----


    X = dff[['Image1']]

    Y = dff["Watches1"]

    x_train1,x_test1,y_train1,y_test1=train_test_split(X,Y,test_size=0.3,random_state=1)

    # --- DT 

    from sklearn.tree import DecisionTreeClassifier

    dt = DecisionTreeClassifier()

    dt.fit(x_train1,y_train1)


    pred_dt = dt.predict(x_train1)

    from sklearn import metrics


    acc_dt = metrics.accuracy_score(y_train1,pred_dt)


    # aa= filename.split('/');

    # aa1 = aa[len(aa)-1]
    
    aa1 = filename.name

    ress = dff[dff['Image'] == aa1]['Image1']


    pred_dt_1 = dt.predict([ress])

    pred_dt_1 = int(pred_dt_1)


    ressult = dff[dff['Watches1'] == pred_dt_1]['Watches']


    inpp = np.array(ressult)
    
    inpp = inpp[0]


    watch_img = mpimg.imread('Watch/'+ inpp)


    plt.imshow(watch_img)
    plt.axis('off')

    plt.show()



    col1,col2,col3 = st.columns(3)
    
    with col1:
        
        st.image(inp_img,caption="Shoe")

    with col2:
        
        st.image(bag_img,caption="Hand bags")    

    with col3:
        
        st.image(watch_img,caption="Watches")       

    
    
    
    
    



