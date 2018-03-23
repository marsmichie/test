/*
 *  This sketch sends random data over UDP on a ESP32 device
 *
 */
#include <WiFi.h>
#include <WiFiUdp.h>

//Sensor stuff
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>

 
#define TCAADDR 0x70
 
/* Assign a unique ID to this sensor at the same time */
Adafruit_HMC5883_Unified mag[8]; 


void displaySensorDetails(Adafruit_HMC5883_Unified *mag)
{
  sensor_t sensor;
  mag->getSensor(&sensor);
  Serial.println("------------------------------------");
  Serial.print  ("Sensor:       "); Serial.println(sensor.name);
  Serial.print  ("Driver Ver:   "); Serial.println(sensor.version);
  Serial.print  ("Unique ID:    "); Serial.println(sensor.sensor_id);
  Serial.print  ("Max Value:    "); Serial.print(sensor.max_value); Serial.println(" uT");
  Serial.print  ("Min Value:    "); Serial.print(sensor.min_value); Serial.println(" uT");
  Serial.print  ("Resolution:   "); Serial.print(sensor.resolution); Serial.println(" uT");  
  Serial.println("------------------------------------");
  Serial.println("");
  delay(500);
}

void tcaselect(uint8_t i) {
  if (i > 7) return;
 
  Wire.beginTransmission(TCAADDR);
  Wire.write(1 << i);
  Wire.endTransmission();  
}
//endsensorstuff


// WiFi network name and password:

/* 
// Marx
const char * networkName = "markus";
const char * networkPswd = "aggro900";
*/
// Mich
const char * networkName = "mich";
const char * networkPswd = "billyt11";

//IP address to send UDP data to:
// either use the ip address of the server or 
// a network broadcast address
const char * udpAddress = "192.168.43.1";
const int udpPort = 9001;

//Are we currently connected?
boolean connected = false;
  WiFiUDP udp;


void setup(){
  Serial.println("Wait 5s for Setup");   
  delay(5000);
  // Initilize hardware serial:
  Serial.begin(115200);
  Serial.println("Serial finished");
  //init magsensors
    mag[1]= Adafruit_HMC5883_Unified(1);
    mag[2]= Adafruit_HMC5883_Unified(2);
    mag[3]= Adafruit_HMC5883_Unified(3);
    mag[4]= Adafruit_HMC5883_Unified(4);
    mag[5]= Adafruit_HMC5883_Unified(5);
    mag[6]= Adafruit_HMC5883_Unified(6);
    mag[7]= Adafruit_HMC5883_Unified(7);
      Serial.println("Magnetfeld");
   for( int sensori = 1; sensori < 8; sensori++ ) {
      tcaselect(sensori);
      if(!mag[sensori].begin())
      {
        Serial.printf("Ooops, no HMC5883 detected on %u",sensori);
      }
  }
  //end magsensors
  Serial.println("Magneticfield sensor setup finished");

 
  
  //Connect to the WiFi network
  connectToWiFi(networkName, networkPswd);
  delay(5000);
}



void loop(){
  //get magneticfielddata
     sensors_event_t event; 
     //The udp library class
    
      float data[8][3];

    //Serial.println(" "); 

   for( int sensori = 1; sensori < 8; sensori++ ) {
      tcaselect(sensori);mag[sensori].getEvent(&event);
     // displaySensorDetails(&mag[sensori]);
      //Serial.printf("Sensor #%u - ",sensori);
      data[sensori][0]=event.magnetic.x;data[sensori][1]=event.magnetic.y;data[sensori][2]=event.magnetic.z;
      Serial.print(sqrt(event.magnetic.x*event.magnetic.x+event.magnetic.y*event.magnetic.y+event.magnetic.z*event.magnetic.z)); Serial.print("uT  ");
     //Serial.print("X: "); Serial.print(event.magnetic.x); Serial.print("  ");
      //Serial.print("Y: "); Serial.print(event.magnetic.y); Serial.print("  ");
      //Serial.print("Z: "); Serial.print(event.magnetic.z); Serial.print("  ");Serial.println("uT");
   }
  //end mf

  //only send data when connected
  if(connected){
    //Send a packet
    udp.beginPacket(udpAddress,udpPort);
    udp.printf("%f#%u#%f#%f#%f#%u#%f#%f#%f#%u#%f#%f#%f#%u#%f#%f#%f#%u#%f#%f#%f#%u#%f#%f#%f#%u#%f#%f#%f",3.14f,1,data[1][0],data[1][1],data[1][2]
    ,2,data[2][0],data[2][1],data[2][2],3,data[3][0],data[3][1],data[3][2],4,data[4][0],data[4][1],data[4][2],5,data[5][0],data[5][1],data[5][2]
    ,6,data[6][0],data[6][1],data[6][2],7,data[7][0],data[7][1],data[7][2]);
    udp.endPacket();
   Serial.println(millis()/1000.0);
  }
  //Wait for 1 second
  delay(10);
}

void connectToWiFi(const char * ssid, const char * pwd){
  Serial.println("Connecting to WiFi network: " + String(ssid));

  // delete old config
  WiFi.disconnect(true);
  //register event handler
  WiFi.onEvent(WiFiEvent);
  
  //Initiate connection
  WiFi.begin(ssid, pwd);

  Serial.println("Waiting for WIFI connection...");
}

//wifi event handler
void WiFiEvent(WiFiEvent_t event){
    switch(event) {
      case SYSTEM_EVENT_STA_GOT_IP:
          //When connected set 
          Serial.print("WiFi connected! IP address: ");
          Serial.println(WiFi.localIP());  
          //initializes the UDP state
          //This initializes the transfer buffer
          udp.begin(WiFi.localIP(),udpPort);
          connected = true;
          break;
      case SYSTEM_EVENT_STA_DISCONNECTED:
          Serial.println("WiFi lost connection");
          connected = false;
          break;
    }
}
