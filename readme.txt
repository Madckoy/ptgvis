mvn compile
mvn exec:java -Dexec.mainClass="com.example.bot.Main" -Dexec.args="--pattern=test_pattern_4.yml --cx=0 --cy=0 --cz=0 --radius=10 --r=2 --ymin=-10"


java -jar target/pgv-1.0-jar-with-dependencies.jar --pattern=test_pattern_4.yml --ox=0 --oy=0 --oz=0 --cx=0 --cy=0 --cz=0 --radius=10 --r=2 --ymin=-10 
java -jar target/pgv-1.0-jar-with-dependencies.jar --pattern=test_pattern.yml --ox=0 --oy=0 --oz=0 --cx=0 --cy=0 --cz=0 --radius=10 --r=10 --ymin=-10 --facing=DOWN

java -jar target/pgv-1.0-jar-with-dependencies.jar --pattern=test_pattern_4.yml --ox=0 --oy=0 --oz=0 --cx=0 --cy=0 --cz=0 --radius=10 --r=2 --ymin=-10 
java -jar target/pgv-1.0-jar-with-dependencies.jar --pattern=test_pattern.yml --ox=0 --oy=0 --oz=0 --radius=10 --r=10 --ymin=-10 --facing=DOWN

java -jar target/pgv-1.0-jar-with-dependencies.jar --pattern=test_pattern_4.yml --ox=-5 --oy=-5 --oz=-5 --cx=0 --cy=0 --cz=0 --radius=10 --r=5 --ymin=-10 



java -jar target/pgv-1.0-jar-with-dependencies.jar --pattern=test_pattern.yml --ox=-10 --oy=-10 --oz=-10 --radius=10 --r=5 --ymin=-10 --facing=DOWN
