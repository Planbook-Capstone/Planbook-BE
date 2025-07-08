
echo "Building app..."
#./mvnw clean package

echo "Deploy files to server..."
scp -r  target/api-gateway.jar root@157.245.135.92:/var/www/api-gateway/

ssh root@157.245.135.92 <<EOF
pid=\$(sudo lsof -t -i:8080)

if [ -z "\$pid" ]; then
    echo "Start server..."
else
    echo "Restart server..."
    sudo kill -9 "\$pid"
fi
cd /var/www/api-gateway
java -jar api-gateway.jar
EOF
exit
echo "Done!"