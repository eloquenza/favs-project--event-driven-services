workspace "FAVS project - Event-driven services" "This is the container diagram showing how our services interact." {

    model {
        customer = person "E-Commerce Customer" "Accessing our e-commerce systems as a single-page application, which implements our business use cases"

        enterprise "FAVS project - Event-driven services" {
            eCommerceSystem = softwaresystem "Our e-commerce system" "Allows customers to log in, create orders for specific products and access their placed orders" {
                discoveryService = container "Discovery service" "Provides service discovery, service registry and load balancing" "Netflix Eureka" "InfrastructureContainer"
                gatewayService = container "Gateway service" "Provides simple way to route all requests to the appropriate business services and binds them to one single access point" "Spring Cloud Gateway" "InfrastructureContainer"
                businessIntelligenceService = container "Business intelligence service" "Provides our business with analytic functions to increase our profit" "Java, Spring WebFlux, Spring Cloud Stream"  "BusinessContainer"
                customerService = container "Customer service" "Provides customer management functionality via a REST API" "Java, Spring WebFlux, Spring Cloud Stream"  "BusinessContainer"
                orderService = container "Order service" "Provides order CRUD functionality via a REST API" "Java, Spring WebFlux, Spring Cloud Stream"  "BusinessContainer"
                productService = container "Product service" "Provides product management functionality via a REST API" "Java, Spring WebFlux" "BusinessContainer"
                customerDB = container "Customer DB" "Stores customer information (username, name, age, ...)" "PostgreSQL" "Database"
                productDB = container "Product DB" "Stores product information (productName, cost)" "PostgreSQL" "Database"
                orderDB = container "Order DB" "Stores order information (orderID, customerID, productID,...)" "PostgreSQL" "Database"
                messageBroker = container "Event store" "Stores our events im topics, allows services to publish/subscribe to them." "Apache Kafka" "Broker"
            }

        }

        # relationships between people and software systems
        uses = customer -> eCommerceSystem "Views account balances, and makes payments using"

        # relationships to/from containers
        customer -> gatewayService "favs.e-commerce.com" "HTTP" "BusinessCommunication"
        gatewayService -> discoveryService "Service discovery" "HTTP" "DiscoveryCommunication"
        customerService -> discoveryService "Service registration" "HTTP" "DiscoveryCommunication"
        productService -> discoveryService "Service registration" "HTTP" "DiscoveryCommunication"
        orderService -> discoveryService "Service registration" "HTTP" "DiscoveryCommunication"
        gatewayService -> customerService "Commands / Queries" "HTTP" "BusinessCommunication"
        gatewayService -> productService "Commands / Queries" "HTTP" "BusinessCommunication"
        gatewayService -> orderService "Commands / Queries" "HTTP" "BusinessCommunication"
        customerService -> messageBroker "CustomerCreated" "Event (JSON)" "BusinessCommunication"
        orderService -> messageBroker "OrderCreated" "Event (JSON)" "BusinessCommunication"
        messageBroker -> businessIntelligenceService "Events" "CustomerCreated, OrderCreated" "BusinessCommunication"
        customerService -> customerDB "SQL queries" "R2DBC" "BusinessCommunication"
        productService -> productDB "SQL queries" "R2DBC" "BusinessCommunication"
        orderService -> orderDB "SQL queries" "R2DBC" "BusinessCommunication"
    }

    views {
        systemlandscape "SystemLandscape" {
            include *
            autoLayout
        }

        systemcontext eCommerceSystem "SystemContext" {
            include *
            autoLayout
        }

        container eCommerceSystem "Containers" {
            include *
        }

        styles {
            element "Person" {
                background #fdf6e3
                color #002b36
                shape Person
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "InfrastructureContainer" {
                background #b58900
                color #000000
            }
            element "BusinessContainer" {
                background #859900
                color #000000
            }
            element "Database" {
                background #073642
                color #ffffff
                shape Cylinder
            }
            element "Broker" {
                background #268bd2
                color #000000
                shape Pipe
            }
            relationship "DiscoveryCommunication" {
                dashed true
                routing Direct
            }
            relationship "BusinessCommunication" {
                dashed false
                routing Direct
            }
        }
    }
}