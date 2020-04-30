# A Cloudstate sample application for reserving hotel/flight/car.

## Instructions for running locally on a mac with minikube.

* Clone this repo.
* clone Cloudstate, git@github.com:cloudstateio/cloudstate.git
* In your cloned cloudstate master, edit /operator/deploy/02-operator-config.yaml
  remove -native- from the proxy configuration section, it should like like this:
  <pre><code>
  # Proxy configuration
    proxy {
      image {
        cassandra = "cloudstateio/cloudstate-proxy-cassandra:latest"
        postgres =  "cloudstateio/cloudstate-proxy-postgres:latest"
        no-store = "cloudstateio/cloudstate-proxy-no-store:latest"
        in-memory = "cloudstateio/cloudstate-proxy-in-memory:latest"
      }
    }
  </code></pre>
* Download and install VirtualBox.
  note: If using Catalina, the install will fail and you will have to grant the permissions, just follow the prompts, then reinstall.
* Install homebrew applications:
  <pre><code>
  brew install minikube
  brew install kubectl
  brew install grpcurl
  </code></pre>
* Start minikube:
  <pre><code>minikube start --vm-driver=virtualbox --cpus 4 --memory 4096</code></pre>
* Now to build and deploy cloudstate itself, so in the cloudstate folder you cloned, in a terminal window (brew install SBT if necessary):
  note: you will be using the internally provided docker service within minikube, you don't even have to have Docker Desktop installed and 
  it's probably better to shut it down for this exercise. The command below needs to be executed in each terminal window you will be using.
  <pre><code>eval $(minikube docker-env)</code></pre>
  <pre><code>sbt -Ddocker.tag=dev</code></pre>
  Now from within SBT, assuming you are only using the inmemory store:
  <pre><code>
  operator/docker:publishLocal
  dockerBuildInMemory publishLocal
  operator/compileK8sDescriptors
  </code></pre>
  and exit SBT.
* Deploy cloudstate:
  <pre><code>
  kubectl create namespace cloudstate
  kubectl apply -n cloudstate -f operator/cloudstate-dev.yaml
  </code></pre>
* Create a stateful store for inmemory. Save the following in a file, name it statefulstore.yaml
  <pre><code>
  apiVersion: cloudstate.io/v1alpha1
  kind: StatefulStore
  metadata:
    name: inmemory
  spec:
    type: InMemory
  </code></pre>
  Now apply it:
  <pre><code>kubectl apply -f statefulstore.yaml</code></pre>
### Whew, now let's install our user functions from this project.
* In a terminal window, switch into where you cloned this project, then:
<pre><code>eval $(minikube docker-env)</code></pre>
<pre><code>mvn clean install</code></pre>
<pre><code>kubectl apply -f deploy/bookings.yaml</code></pre>
* Now this sample is running with cloudstate on your local kubernetes environment. Let's expose a port in order to send GRPC commands and test it out.
<pre><code>kubectl expose deployment bookings-deployment --port=8013 --type=NodePort</code></pre>
* Issue the following command and record the URL you'll use to interact with your service:
<pre><code>minikube service bookings-deployment --url</code></pre>
Let's assume this resulted in the following:
<pre><code>http://192.168.99.114:32149</code></pre>
* Some GRPC discovery you can perform, to show the capabilities of this service:
<pre><code>
grpcurl -plaintext 192.168.99.114:32149 describe
grpcurl -plaintext 192.168.99.114:32149 list
grpcurl -plaintext 192.168.99.114:32149 list flightservice.FlightBookingService
</code></pre>
* If everything is ok to the point, time to really do something, like reserve a flight:
<pre><code>
  grpcurl -d '{"reservationId": "1234", "userId": "sean", "flightNumber": "ua909"}' \
    192.168.99.113:30070 flightservice.FlightBookingService.ReserveFlight
</code></pre>
* And view that reservation:
<pre><code>
  grpcurl -plaintext -plaintext -d '{"reservationId": "1234"}' \
  192.168.99.114:32149 flightservice.FlightBookingService.GetFlightReservation