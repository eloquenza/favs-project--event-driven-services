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

Users of this e-commerce ship must register and create themselves a `Customer` account to place orders on products.

### Updating a customer's account information

Customers are able to change their account information, this includes their first and last name, their username, their age, their birthday (in case of an faulty entry in the first place), their address.

### Customer account deletion

Customers is able to delete their account for our e-commerce shop.
This should ideally delete all their information, including order information, if we are not otherwise, i.e. legally, obliged to do so.

### Browsing the FAVS-commerce product catalog

A customer is able to view all the products offered by the FAVS-commerce store in order to choose products to order from this catalog.
Ideally, even if not present in the first version, they should be able to search in this catalog and filter by any specific product categories.

### Adding a product to the warehouse

Other retailers or a department store team is able to add new products to the department store so that they can be displayed to the customer.
For this, they need to specify what the name and description of the product is and how much it costs.

### Updating a product's details

The owners of the product are able to update the product's detailed information by providing the new information into the appropriate form for it.
This includes it's name, it's description and it's cost.

### Placing an order

A customer is able to place an order for a product.
Ideally a order contains multiple products, but as a first version, having only one product entry is allowed.
This should create an order for the specified product.
Before that order will be shipped and delivered to their home door, a customer needs to pay.

### Updating an order's details

A customer is allowed to change the details of an order.

#### Changing the products of an order

A customer might want to order an different product than the one he selected.

#### Paying for an order

A customer is able to pay for their order so that the store can initiate shipping.

#### Cancelling an order

A customer is able to cancel their order, even after they have paid, if they wish.
If an order is already in "SHIPPED" / "DELIVERED" status, this should no longer be possible, since at this point the customer must first return the product.

#### Deleting an order

A customer is able to cancel an order if they wish.
This should only be possible if the order has not yet been shipped so that the store is still able to refund the customer if they return the product.

## Strategic design

From these business use case requirements, the following bounded contexts can be formed:

* Customer component:
  * Manages the accounts of all customers
* Order component
  * Takes the order placements and manages their state transitions
    * This includes notifications to the appropriate shipping company for shipping and delivering to the customer
  * Manages the orders for all customers
* Product component
  * Manages our warehouse, i.e. available products, quantity of products
  * Ideally provides some kind of interface for retailers to manage their product catalog
* Business intelligence component
  * Customer analysis: provide marketing/business intelligence team with customer analysis, i.e. what kind of customers are using our shop
  * Product analysis: provide marketing/business intelligence team with product analysis, i.e. which customers are interested in what products and what products are actually bought

## Core subdomains

From these bounded contexts, the following form the most important business subdomains:

* Customer component
* Order component
* Product component

The order component needs to define their own `Customer` and `Product` domain model as they need access to specific information from both domain models.

## Generic subdomains

From the bounded contexts, the following simply faciliate the business:

* Business intelligence component

## Context mapping

The identified contact points between our bounded contexts are:

* `Customer` entity in the `Order` context
* `Product` entity in the `Order` context

The pattern decided on for mapping these models between the contexts is: customer/supplier.
Our downstream context, `Order`, uses whatever the `Product` and `Customer` upstream teams have defined as the interface.
This is almost an "conformist" approach to these mappings with the exception that our `Order` context demands that atleast the identifier of domain entities is provided.

## Mapping bounded contexts to microservices

Each of these identified subdomains define a bounded context.
For each bounded context, we will implement a micro-service.
This is not necessarily the best course of action, but for such a a small exemplary system, it is okay to do so.

This means:

* Customer subdomain is implemented by `customer-service`
* Order subdomain is implemented by `order-service`
* Product subdomain is implemented by `product-service`
* Business intelligence subdomain is implemented by `business-intelligence-service`
