package com.demo.userMSDemo.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Random;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.demo.userMSDemo.Repository.UserRepo;
import com.demo.userMSDemo.model.OrderDetails;
import com.demo.userMSDemo.model.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
public class UserMSController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMSController.class);

	@Autowired
	UserRepo repo;
  
		
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/hello")
	public String getHello() {
		return "Hello World";
	}

	@GetMapping("/users")
	public List<User> getAllUser() {
		return repo.findAll();
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getSingleUser(@PathVariable Integer id) {
		Optional<User> user = repo.findById(id);
		if (user.isPresent()) {
			return ResponseEntity.ok(user.get());
		}

		return ResponseEntity.notFound().build();
	}

	@PostMapping(path = "/addUser", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getSingleUser(@RequestBody User user,
			@RequestHeader(name = "city", required = true, defaultValue = "indore") String city,
			@RequestHeader(value = "state", required = true) String state) throws URISyntaxException {
		User newUser = repo.save(user);
		System.out.println("test uri" + new URI("abc"));
		return ResponseEntity.created(new URI(newUser.getId().toString())).body(newUser);

	}

	@HystrixCommand(fallbackMethod = "getOrdersFromfallback", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
			@HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE") })
	@GetMapping("/hystrixDemoEndpoint")
	public String hystrixDemoEndpoint() {
		LOGGER.info("about to call orderms");
		throw new RuntimeException("Not Available");
	}
	
     
	  
	  @GetMapping("/getOrdersGetForEntityTest")
	  public ResponseEntity<OrderDetails> callOrderServiceTest() throws MalformedURLException {
		  URI serviceUrl = null;
		  List<ServiceInstance> list = discoveryClient.getInstances("orderms");
		  if (list != null && list.size() > 0 ) {
			  Random ran = new Random();
			  int x = ran.nextInt(list.size());
			  serviceUrl =  list.get(x).getUri();
		    }
		  
		  if(null!= serviceUrl) {
			  String url = serviceUrl.toURL().toString()+"/orders";
			  ResponseEntity<OrderDetails> response = restTemplate.exchange(url, HttpMethod.GET, null, OrderDetails.class);
			  return response;
			  
		  }
		  return null;
	  }
	  
	  

	@GetMapping("/getOrders")
	public String getOrders() {
		LOGGER.info("about to call orderms");
		return restTemplate.getForObject("http://orderms/orders", String.class);
	}
	
	@PostMapping(path = "/addOrderTest", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderDetails> addOrder(@RequestBody OrderDetails orderDetails) {
		LOGGER.info("inside addOrder methon in User ms");
		HttpEntity<OrderDetails> httpEntity = new HttpEntity<>(orderDetails, null);
		return restTemplate.exchange("http://orderms/addOrder",HttpMethod.POST,httpEntity,OrderDetails.class);
		
		//return restTemplate.postForEntity("http://orderms/addOrder",orderDetails,OrderDetails.class);
	}
	

	private String getOrdersFromfallback() {
		LOGGER.info("fallback of orderms");
		return "Falback response";
	}

}
