
echo "Building app..."
#./mvnw clean package

echo "Deploy files to server..."
scp -r  target/gateway.jar root@14.225.210.212:/var/www/gateway/

ssh root@14.225.210.212 <<EOF
pid=\$(sudo lsof -t -i:8080)

if [ -z "\$pid" ]; then
    echo "Start server..."
else
    echo "Restart server..."
    sudo kill -9 "\$pid"
fi
cd /var/www/gateway
java -jar gateway.jar
EOF
exit
echo "Done!"