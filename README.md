# LoRaWAN-Kommunikation mit MQTT-Client
Dieses Projekt demonstriert die bidirektionale Kommunikation zwischen einem Arduino-Gerät mit LoRaWAN-Modul und einem MQTT-Client über The Things Network (TTN). Es dient als Beispiel für die Implementierung von Up- und Downlinks in einem LoRaWAN-Netzwerk und die Verarbeitung der Daten mit einem MQTT-Client.

## Komponenten
* **Arduino-Gerät:** Ein Arduino-Board (Uno) mit einem LoRaWAN-Modul (Lora BEE v1.1).
* **LoRaWAN-Gateway:** Ein Gateway, das die LoRaWAN-Nachrichten empfängt und an TTN weiterleitet.
* **The Things Network (TTN):** Eine Plattform zur Verwaltung von LoRaWAN-Geräten und -Netzwerken.
* **MQTT-Client:** Eine Java-Anwendung, die MQTT-Nachrichten abonniert und veröffentlicht.

## Funktionsweise
1. **Uplink:** Das Arduino-Gerät sendet periodisch Nachrichten mit einem Zählerwert über LoRaWAN an TTN.
2. **TTN:** TTN empfängt die Uplink-Nachrichten und leitet sie an den MQTT-Client weiter.
3. **MQTT-Client:** Der MQTT-Client empfängt die Uplink-Nachrichten, extrahiert den Zählerwert und berechnet die Round-Trip-Time (RTT).
4. **Downlink:** Der MQTT-Client sendet periodisch Downlink-Nachrichten mit einem Zeitstempel und einer Sequenznummer an das Arduino-Gerät über TTN.
5. **Arduino-Gerät:** Das Arduino-Gerät empfängt die Downlink-Nachrichten und gibt die Sequenznummer aus.


## Installation
1. **Arduino-Gerät:** Installieren Sie die Arduino IDE und die notwendigen Bibliotheken für das LoRaWAN-Modul (z. B. LMIC). Konfigurieren Sie das Arduino-Gerät mit den Zugangsdaten für TTN (DevEUI, AppEUI, AppKey).
2. **TTN:** Erstellen Sie ein Konto bei TTN und registrieren Sie Ihr LoRaWAN-Gateway und -Gerät.
3. **MQTT-Client:** Installieren Sie Java und die benötigten Bibliotheken (z. B. HiveMQ MQTT Client, Jackson). Konfigurieren Sie den MQTT-Client mit den Zugangsdaten für TTN.

## Verwendung
1. Flashen Sie den Arduino-Code auf das Arduino-Gerät.
2. Starten Sie den MQTT-Client.
3. Beobachten Sie die Up- und Downlink-Nachrichten im TTN-Dashboard und in der Konsolenausgabe des MQTT-Clients.