#include <EEPROM.h>
#include <Arduino.h>
#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>

#include <Firebase_ESP_Client.h>

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"



// Insert Firebase project API Key
#define API_KEY "AIzaSyDn4j2INFLK1bdSBu-sQjbni6SP0x0ssY0"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "smartbikehelmet-a1b95-default-rtdb.firebaseio.com/" 

//Define Firebase Data object
FirebaseData fbdo;
FirebaseData buzzdat;
FirebaseData dat;

Adafruit_MPU6050 mpu;


FirebaseAuth auth;
FirebaseJson json;
FirebaseConfig config;

//MPU6050 mpu; 


unsigned long sendDataPrevMillis = 0;
unsigned long buttonPressMillis = 0; // Time when button was last pressed
int count = 0;
bool signupOK = false;

#define BUTTON_PIN D3  // Change to D1 pin for the button
#define BUTTON2_PIN D4  // Change to D1 pin for the button
#define BUTTON_DISABLE D7
#define BUTTON2_DISABLE D8
#define INDICATOR D5

#define EEPROM_SIZE 512

#define BUZZER_PIN D6

float minorThresh = 15.0f;

String WIFISSID = "";
String WIFIPASS = "";

void writeStringToEEPROM(const String &str)
{
    EEPROM.begin(EEPROM_SIZE);
    EEPROM.write(0,1); 
    Serial.println("writing to memory");
    int len = str.length();
    if (len > EEPROM_SIZE - 1) len = EEPROM_SIZE - 1;

    for (int i = 0; i < len; i++) {
        EEPROM.write(i+1, str[i]);
    }
    EEPROM.write(len+1, '\0');  // Null-terminate
    EEPROM.commit();
    EEPROM.end();
    Serial.println("String stored in EEPROM!");
}

String readStringFromEEPROM(int addrOffset)
{
  EEPROM.begin(EEPROM_SIZE);
    char buffer[EEPROM_SIZE];
    
    for (int i = 0; i < EEPROM_SIZE; i++) {
        buffer[i] = EEPROM.read(addrOffset + i);
        if (buffer[i] == '\0') break;
    }
    EEPROM.end();
    return String(buffer);
}

void setup(){
  delay(1200);
  Serial.begin(115200);
  
  pinMode(BUTTON_PIN, INPUT);
  pinMode(BUTTON2_PIN, INPUT);
  pinMode(BUTTON_DISABLE, OUTPUT);
  pinMode(BUTTON2_DISABLE, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  pinMode(INDICATOR, OUTPUT);
  digitalWrite(INDICATOR, LOW);
  digitalWrite(BUZZER_PIN, HIGH);
  digitalWrite(BUTTON_DISABLE, LOW);
  digitalWrite(BUTTON2_DISABLE, LOW);
  
  EEPROM.begin(EEPROM_SIZE);
  int flag = EEPROM.read(0);
  Serial.println(flag);
  String memdat = flag == 1 ? readStringFromEEPROM(1) : "";
  EEPROM.end();
  
  if(memdat == ""){
    Serial.println("Waiting for input");
    while(Serial.available() == 0){}
    memdat = Serial.readString()+'\n';
    writeStringToEEPROM(memdat);
  }
    Serial.println(memdat);
    Serial.println(memdat.length());
    int index = memdat.indexOf(' ');
    WIFISSID = memdat.substring(0, index);
    WIFIPASS = memdat.substring(index+1, memdat.length()-1);

  digitalWrite(INDICATOR, HIGH);
  WiFi.begin(WIFISSID, WIFIPASS);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    checkReset();
    delay(300);
  }
  
  digitalWrite(INDICATOR, LOW);
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("ok");
    signupOK = true;
    
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  
  
//  mpu.initialize();
  if (!mpu.begin()) {
    Serial.println("Failed to find MPU6050 chip");
    while (1) {
      delay(10);
    }
  }
  Serial.println("MPU6050 Found!");

  mpu.setAccelerometerRange(MPU6050_RANGE_8_G);
  mpu.setGyroRange(MPU6050_RANGE_500_DEG);
  mpu.setFilterBandwidth(MPU6050_BAND_21_HZ); 

  if(Firebase.RTDB.getString(&dat, "/sensor/thresh")){
    String dataa = dat.stringData();
    float led = dataa.toFloat();
    minorThresh = led;
  }

  Serial.println("");
  
}

void checkReset(){
  if(Serial.available() > 0){
  String memdat = Serial.readString();
  Serial.println(memdat);
  if(memdat == "RESET\n"){
    EEPROM.begin(EEPROM_SIZE);
    EEPROM.write(0, 0);
    EEPROM.commit();
    EEPROM.end();   
    WiFi.disconnect();
    ESP.reset();
    return;
  }
 }
}

void loop(){
 int analogValue = analogRead(0);
 float voltage = 0.0048*analogValue;
 int currentButtonState = digitalRead(BUTTON_PIN);
 int currentButton2State = digitalRead(BUTTON2_PIN);

 checkReset();

 if(currentButtonState == LOW){
  Serial.println("pressed button 1");
  digitalWrite(BUTTON_DISABLE, HIGH);
  delay(10);
  digitalWrite(BUTTON_DISABLE, LOW);
  if(Firebase.RTDB.setFloat(&fbdo, "sensor/buttonState", 1)){
      Serial.println("Button State 1 PASSED");
    } else {
      Serial.println("Button state update failed");
      Serial.println("REASON: " + fbdo.errorReason());
    }
 }
 if(currentButton2State == LOW){
  Serial.println("pressed button 2");
  digitalWrite(BUTTON2_DISABLE, HIGH);
  delay(10);
  digitalWrite(BUTTON2_DISABLE, LOW);
  if(Firebase.RTDB.setFloat(&fbdo, "sensor/buttonState2", 1)){
      Serial.println("Button State 2 PASSED");
    } else {
      Serial.println("Button state update failed");
      Serial.println("REASON: " + fbdo.errorReason());
    }
 }
// if(Firebase.ready() && signupOK && (millis() - sendDataPrevMillis >= 5000 || sendDataPrevMillis == 0)){
//  sendDataPrevMillis = millis();
//  if(Firebase.RTDB.getString(&buzzdat, "/sensor/buzzerCommand")){
//    String dataa = buzzdat.stringData();
//    int led = dataa.toInt();
//    if(led == 1){
//      digitalWrite(BUZZER_PIN, LOW);
//      Serial.println("Buzzer is ON");
//    }
//    else if (led == 0){
//      digitalWrite(BUZZER_PIN, HIGH);
//      Serial.println("Buzzer is OFF");
//    }
//  }
// }
 
sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);

// Serial.print("Battery : ");
// Serial.print((voltage/3.7) * 100);
// Serial.print("%  |  ");
// 
//  /* Print out the values */
//  Serial.print("Acceleration X: ");
//  Serial.print(a.acceleration.x);
//  Serial.print(", Y: ");
//  Serial.print(a.acceleration.y);
//  Serial.print(", Z: ");
//  Serial.print(a.acceleration.z);
//  Serial.print("  |  ");
//
//  Serial.print("Gyroscope X: ");
//  Serial.print(g.gyro.x);
//  Serial.print(", Y: ");s
//  Serial.print(g.gyro.y);
//  Serial.print(", Z: ");
//  Serial.print(g.gyro.z);
//  Serial.println();
  
    float accelX = float(a.acceleration.x);
    float accelY = float(a.acceleration.y);
    float accelZ = float(a.acceleration.z);
    
 if (Firebase.ready() && signupOK && (accelX*accelX + accelY*accelY + accelZ*accelZ)>minorThresh*minorThresh){

    Serial.println("Sending data cuz crashed");
   // Write dummy accelerometer data to the database
    if (Firebase.RTDB.setFloat(&fbdo, "sensor/accelerometer/x", accelX)) {
    } else {
      Serial.println("Accel X Data FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    
    if (Firebase.RTDB.setFloat(&fbdo, "sensor/accelerometer/y", accelY)) {
    } else {
      Serial.println("Accel Y Data FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    
    if (Firebase.RTDB.setFloat(&fbdo, "sensor/accelerometer/z", accelZ)) {
    } else {
      Serial.println("Accel Z Data FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    if (Firebase.RTDB.setFloat(&fbdo, "sensor/gyroscope/x", float(g.gyro.x))) {
    } else {
      Serial.println("Gyro data X FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    if (Firebase.RTDB.setFloat(&fbdo, "sensor/gyroscope/y", float(g.gyro.y))) {
    } else {
      Serial.println("Gyro data Y FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    if (Firebase.RTDB.setFloat(&fbdo, "sensor/gyroscope/z", float(g.gyro.z))) {
    } else {
      Serial.println("Gyro data Z FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    count++;
  }
}
