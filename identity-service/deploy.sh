
echo "Building app..."
#./mvnw clean package

echo "Deploy files to server..."
scp -r  target/identity.jar root@14.225.210.212:/var/www/identity/

ssh root@14.225.210.212 <<EOF
pid=\$(sudo lsof -t -i:8081)

if [ -z "\$pid" ]; then
    echo "Start server..."
else
    echo "Restart server..."
    sudo kill -9 "\$pid"
fi
cd /var/www/identity
java -jar identity.jar
EOF
exit
echo "Done!"