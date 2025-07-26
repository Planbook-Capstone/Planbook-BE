echo "Building app..."
#./mvnw clean package

echo "Deploy files to server..."
scp -r  target/purchase.jar root@14.225.210.212:/var/www/purchase/

ssh root@14.225.210.212 <<EOF
pid=\$(sudo lsof -t -i:8000)

if [ -z "\$pid" ]; then
    echo "Start server..."
else
    echo "Restart server..."
    sudo kill -9 "\$pid"
fi
cd /var/www/purchase
java -jar purchase.jar
EOF
exit
echo "Done!"