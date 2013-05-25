copy target\webpnp.war \bin\tjws_1_103\WebServer\webapps
pscp -l pi -pw raspberry target\webpnp.war raspberrypi:webpnp.war
