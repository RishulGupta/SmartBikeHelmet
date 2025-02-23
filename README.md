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

![image](https://github.com/user-attachments/assets/787eb4e5-e172-4347-b3b2-59174a4d90aa)
![image](https://github.com/user-attachments/assets/55639c02-bf99-4afa-a422-c374cc6578de)
![image](https://github.com/user-attachments/assets/e1192ab4-90db-4849-b5c2-9c2c26493eb8)
![image](https://github.com/user-attachments/assets/8b095078-7596-47f7-a26e-8ff62c524e2a)

## ⚙️Hardware
![image](https://github.com/user-attachments/assets/78cf851a-0bec-4a1d-b583-b4311d7d6ac8)






