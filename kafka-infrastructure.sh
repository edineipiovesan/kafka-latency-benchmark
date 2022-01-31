# Doc: https://docs.confluent.io/current/quickstart/ce-docker-quickstart.html
git clone https://github.com/confluentinc/cp-all-in-one
cd cp-all-in-one || exit
git checkout 7.0.1-post
cd cp-all-in-one/ || exit
docker-compose up -d
docker-compose ps