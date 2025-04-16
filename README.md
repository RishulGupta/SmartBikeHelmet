# Smart Bike Helmet 🚴‍♂️📱  

The **Smart Bike Helmet** app combines cutting-edge **IoT** and **mobile technology** to ensure enhanced rider safety and convenience. This Android application integrates with hardware sensors and Firebase to provide advanced features such as crash detection, emergency SOS alerts, and real-time location sharing.  

---  

## 📲 Features  

### **Crash Detection**  
- Utilizes accelerometer data to detect crashes.  
- Differentiates between:  
  - **Minor crashes**: Sends notifications to check in on the rider's status.  
  - **Major crashes**: Sends SOS alerts with live location to emergency contacts.  

### **Emergency SOS Alerts**  
- Automatically sends SOS messages in case of major crashes, including:  
  - Rider’s live location.  
  - Notifications to emergency contacts, ambulance services, and a women’s helpline.  
- **Manual SOS** option via a connected hardware button for added flexibility.  

### **Real-Time Monitoring**  
- Real-time data synchronization with **Firebase Realtime Database** for monitoring sensor inputs and emergency triggers.  

### **Hands-Free Riding**  
- Access to features like:  
  - **Navigation** with maps.  
  - Listening to music.  
  - Hands-free calling for a seamless riding experience.  

---  

## 🛠️ Tech Stack  

### **Frontend**  
- Developed in **Android (Kotlin)**.  

### **Backend & IoT**  
- **Firebase Realtime Database** for data storage and synchronization.  
- Integration with IoT hardware for sensor data processing:  
  - **Accelerometer** for crash detection.  
  - **GPS module** for live location tracking.  
  - **Button state** for manual SOS activation.  

### **Hardware Components**  
- **ADXL-335 Accelerometer**: For crash detection.  
- **HC-05 Bluetooth Module**: For wireless communication.  
- **GPS Module**: For real-time location tracking.  
- **ESP32/Arduino Microcontroller**: For processing sensor inputs and transmitting data to Firebase.  

---  

## 🎉 How It Works  

### **Crash Detection**  
1. Reads accelerometer data from Firebase.  
2. Calculates crash magnitude thresholds:  
   - **Minor crashes**: Trigger a notification to the rider for a check-in.  
   - **Major crashes**: Automatically sends SOS alerts to emergency services with location data.  

### **Button-Activated SOS**  
- A hardware button’s state (`0` or `1`) is monitored via Firebase.  
- **Button pressed**: Sends an immediate SOS alert with the rider’s location.  

### **SOS Alerts**  
- **Minor Crashes**: Sends a notification asking the rider to confirm safety.  
- **Major Crashes**: Sends emergency SOS messages to pre-configured contacts, along with live location details.  

---  
## 📲 User Interface
<img src="https://github.com/user-attachments/assets/eaeb3a21-23f5-4c0a-8283-55f6e3c33d4b" width="300"/>

<img src="https://github.com/user-attachments/assets/9f757736-0e57-4af8-8b8b-6dd6b4985421" width="300"/>




<img src="https://github.com/user-attachments/assets/e1192ab4-90db-4849-b5c2-9c2c26493eb8" width="300"/>
<img src="https://github.com/user-attachments/assets/8b095078-7596-47f7-a26e-8ff62c524e2a" width="300"/>

<img src="https://github.com/user-attachments/assets/1d762eb9-4722-4d4a-89a8-a2220d6c77ed" width="300"/>


## ⚙️Hardware
<img src="https://github.com/user-attachments/assets/62a790f6-6766-4af7-baef-b0b53ec076b0" width="300"/>








