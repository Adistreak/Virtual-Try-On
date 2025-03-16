import os
import cv2
import numpy as np
import cvzone
from cvzone.PoseModule import PoseDetector  # Updated Pose Module

# Initializing the Pose detector
detector = PoseDetector()

# Camera capture setup
cap = cv2.VideoCapture(0)

if not cap.isOpened():
    print("Error: Camera not accessible. Check permissions.")
    exit()

# Shirt folder path
shirtFolderPath = "/Users/adityasoni/PycharmProjects/VirtualTryOn /pythonProject2/.venv/Resources/Shirts"
listShirts = os.listdir(shirtFolderPath)

# Parameters for shirt resizing
fixedRatio = 262 / 190  # Adjust based on shoulder width
shirtRatioHeightWidth = 581 / 440
imageNumber = 0

# Load button images
imgButtonRight = cv2.imread("Resources/button.png", cv2.IMREAD_UNCHANGED)
imgButtonLeft = cv2.flip(imgButtonRight, 1)

# Initialize counters for button interaction
counterRight = 0
counterLeft = 0
selectionSpeed = 10

while True:
    # Capture frame from camera
    success, img = cap.read()
    if not success:
        print("Error: Failed to capture frame from camera.")
        break

    # Detect pose and get landmarks
    img = detector.findPose(img)
    lmList, bboxInfo = detector.findPosition(img, draw=False)

    if lmList:
        lm11 = lmList[11][0:2]  # Left shoulder
        lm12 = lmList[12][0:2]  # Right shoulder

        # Ensure landmarks are valid and calculate shoulder width
        shoulderWidth = abs(lm12[0] - lm11[0])
        if shoulderWidth <= 0:
            print("Invalid shoulder width detected. Skipping frame...")
            continue

        # Select shirt image based on current imageNumber
        imgShirt = cv2.imread(os.path.join(shirtFolderPath, listShirts[imageNumber]), cv2.IMREAD_UNCHANGED)

        # Resize shirt based on shoulder width
        shirtWidth = int(shoulderWidth * fixedRatio)
        shirtHeight = int(shirtWidth * shirtRatioHeightWidth)
        imgShirt = cv2.resize(imgShirt, (shirtWidth, shirtHeight))

        # Calculate shirt overlay position
        offsetX = int((lm11[0] + lm12[0]) / 2 - shirtWidth / 2)
        offsetY = int(lm11[1] - shirtHeight / 4)

        try:
            # Overlay shirt
            img = cvzone.overlayPNG(img, imgShirt, (offsetX, offsetY))
        except Exception as e:
            print(f"Error overlaying shirt: {e}")

        # Overlay buttons
        img = cvzone.overlayPNG(img, imgButtonRight, (1074, 293))
        img = cvzone.overlayPNG(img, imgButtonLeft, (72, 293))

        # Detect gesture for changing shirt
        if lmList[16][1] < 300:  # Right hand gesture
            counterRight += 1
            cv2.ellipse(img, (139, 360), (66, 66), 0, 0, counterRight * selectionSpeed, (0, 255, 0), 20)
            if counterRight * selectionSpeed > 360:
                counterRight = 0
                if imageNumber < len(listShirts) - 1:
                    imageNumber += 1
        elif lmList[15][1] > 900:  # Left hand gesture
            counterLeft += 1
            cv2.ellipse(img, (1138, 360), (66, 66), 0, 0, counterLeft * selectionSpeed, (0, 255, 0), 20)
            if counterLeft * selectionSpeed > 360:
                counterLeft = 0
                if imageNumber > 0:
                    imageNumber -= 1
        else:
            counterRight = 0
            counterLeft = 0

    # Display the resulting frame
    cv2.imshow("Virtual Try-On", img)

    # Exit when 'q' is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release resources
cap.release()
cv2.destroyAllWindows()
