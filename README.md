## Smart Home
In this project a smart home is designed and built using Raspberry pi3. The following sensors and hardware have been used: 
- RGB LEDS
- MQ2 Gas sensor
- DHT11 Temprature and humidity sensor
- PIR motion detector
- RFID sensor and tag
- Fan
- Relay
- Micro servo motor

The code is written in Python language. An android application is also designed and created so that the hardware could be controlled via a smart phone, through a local platform called Thing Talk.

![](C:\Users\Shakiba\Desktop\Smart home\pics\1.jpg)

### The house lights
The house has 3 RGB LEDS. Using the mobile app, the LEDs can be controlled. They can be turned on or off and their colors could be changed to green, blue or yellow. 

### Fire alarm
The MQ2 sensor has been used to detect smoke in the house. Whenever smoke is detected, an alarm would be sent to the user on their phone.They would receive a notification that lets them know smoke is detected no matter where they are. 

### Ventilation
With DHT11 sensor, the temprature and humidity of the house is monitored real time. The percentage of each element is shown in the computer and a chart in the mobile app depicts the changes. 
The fan of the house works in 2 modes. Automatic and manual. When set on Automatic mode, the fan automatically turns on or off depending on the house temprature which is monitored by DHT11. In manual mode, the user can turn it on or off using the mobile app.A relay is connected to the fan through which we decide whether to turn it on or off.

### Motion detection
The PIR sensor detects any movement. It is installed near the door and when someone enters the house, the LEDs turn to red for a few seconds before changing back to their previous state.

### RFID
The RFID is used right by the door so that the house owner can open the door using their special card or tag. Whenever the door opens, they also receive a notification on their phone. In addition, it is possible to open and close the door using the mobile app as well, no matter where they are. The micro servo motor is used to open and close the door in the replica.
