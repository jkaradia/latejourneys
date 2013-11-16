package com.latejourneys.service;


import org.springframework.stereotype.Service;
 
/**
 * @Service enables the class to be used as a Spring service
 * @Transactional enables transaction support for this class
 */
@Service("springService")
public class ArithmeticService {
   
  
 /**
  * Adds two numbers
  */
 public Integer add(Integer operand1, Integer operand2) {
  // A simple arithmetic addition
  return operand1 + operand2;
 }
  
}
