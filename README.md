
# declare-transit-movement-cancellation-frontend

This service allows a user to cancel a transit movement.

Service manager port: 9495

### Testing

Run unit tests:
<pre>sbt test</pre>  
Run integration tests:  
<pre>sbt it:test</pre>  
or
<pre>sbt IntegrationTest/test</pre>  

### Running manually or for journey tests

Note: The cancellation frontend is tested as part of the departures journey tests.

    sm --start CTC_TRADERS_DEPARTURE_ACCEPTANCE -r
    sm --stop DECLARE_TRANSIT_MOVEMENT_CANCELLATION_FRONTEND
    sbt run

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

