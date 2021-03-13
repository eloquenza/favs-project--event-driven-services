# Domain driven-design process

This document describes the domain of the FAVS-commerce system.

## Overview of the system

FAVS-commerce plans to provide event-driven architectural solution for an e-commerce shop to handle large volumes of customers and their orders.
As usual in an e-commerce system, the heart of this business are the customers.
Customers are able to place orders for specific products, pay for these and then wait for the company to ship & deliver these products to their home.

As trying to build a large, unified model for the entire enterprise model is hard, we are applying the "Domain-driven design" (DDD) technique in order to handle the upcoming complexity.

Normally, there is a single definition for each needed business entity like a customer, but getting all different organizational units to agree to such a model is not an easy task.

Either the model is too overly complex for a specific sub-domain, or it might be confusing for some sub-domains as there might be a different meaning for a specific domain term.

DDD helps with this issue as each subdomain gets to define their own seperate domain model that fits their needs exactly.

## Business use cases

### Customer registration / Creating a customer
### Updating a customer's account information
### Customer account deletion

### Placing an order
### Updating an order's details
#### Changing the products of an order
#### Paying for an order
#### Canceling an order
### Deleting an order

### Adding a product to the warehouse
### Updating a product's details
#### Updating a product's name
#### Updating a product's cost

## Strategic design

### Customer component
### Order component
### Product component
### Business intelligence component

## Core subdomains

### Customer component
### Order component
### Product component

## Generic subdomains

### Business intelligence component

## Bounded contexts

Each of these identified subdomains define a bounded context.
For each bounded context, we will implement a micro-service.
This is not necessarily the best course of action, but for such a a small exemplary system, it is okay to do so.

This means:

* Customer subdomain is implemented by `customer-service`
* Order subdomain is implemented by `order-service`
* Product subdomain is implemented by `product-service`
* Business intelligence subdomain is implemented by `business-intelligence-service`

## Context mapping

The identified contact points between our bounded contexts are:

* `Customer` entity in the `Order` context
* `Product` entity in the `Order` context

The pattern decided on for mapping these models between the contexts is: customer/supplier.
Our downstream context, `Order`, uses whatever the `Product` and `Customer` upstream teams have defined as the interface.
This is almost an "conformist" approach to these mappings with the exception that our `Order` context demands that atleast the identifier of domain entities is provided.
