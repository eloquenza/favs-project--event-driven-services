workspace "FAVS project - Event-driven services" "This is the container diagram showing how our services interact." {

    model {
        enterprise "FAVS project - Event-driven services" {
            eCommerceSystem = softwaresystem "Our e-commerce system" "Allows customers to log in, create orders for specific products and access their placed orders" {
                discoveryService = container "Discovery service" "Provides service discovery, service registry and load balancing" "Netflix Eureka" "DiscoveryServiceContainer"
                gatewayService = container "Gateway service" "Provides simple way to route all requests to the appropriate business services and binds them to one single access point" "Spring Cloud Gateway" "GatewayServiceContainer"
                businessIntelligenceService = container "Business intelligence service" "Provides our business with analytic functions to increase our profit" "Java, Spring WebFlux, Spring Cloud Stream"  "BusinessContainer"
                customerService = container "Customer service" "Provides customer management functionality via a REST API" "Java, Spring WebFlux, Spring Cloud Stream"  "BusinessContainer"
                orderService = container "Order service" "Provides order CRUD functionality via a REST API" "Java, Spring WebFlux, Spring Cloud Stream"  "BusinessContainer"
                productCommandService = container "Product service (CQRS write model)" "Provides product creation and update functionality via a REST API" "Java, Spring WebFlux" "BusinessContainer"
                productQueryService = container "Product service (CQRS read model)" "Provides product getter functionality via a REST API, holds all products in an in-memory DB" "Java, Spring WebFlux" "BusinessContainer"
                customerDB = container "Customer DB" "Stores customer information (username, name, age, ...)" "PostgreSQL" "Database"
                productDB = container "Product DB" "Stores product information (productName, cost)" "PostgreSQL" "Database"
                orderDB = container "Order DB" "Stores order information (orderID, customerID, productID,...)" "PostgreSQL" "Database"
                messageBroker = container "Event store" "Stores our events im topics, allows services to publish/subscribe to them." "Apache Kafka" "Broker"
            }

        }

        # relationships to/from containers
        gatewayService -> discoveryService "Service discovery" "HTTP" "DiscoveryCommunication"
        gatewayService -> customerService "Commands / Queries" "HTTP" "BusinessCommunication"
        gatewayService -> productCommandService "Commands" "HTTP" "BusinessCommunication"
        gatewayService -> productQueryService "Queries" "HTTP" "BusinessCommunication"
        gatewayService -> orderService "Commands / Queries" "HTTP" "BusinessCommunication"
        productCommandService -> messageBroker  "Publishes events" "" "BusinessCommunication"
        customerService -> messageBroker  "Publishes events" "" "BusinessCommunication"
        orderService -> messageBroker "Publishes events" "" "BusinessCommunication"
        messageBroker -> businessIntelligenceService "Subscribes to events" "" "BusinessCommunication"
        messageBroker -> orderService "Subscribes to events" "" "BusinessCommunication"
        customerService -> customerDB "SQL queries" "R2DBC"
        messageBroker -> productQueryService "Subscribes to events" "" "BusinessCommunication"
        customerService -> customerDB "SQL queries" "R2DBC" "BusinessCommunication"
        productCommandService -> productDB "SQL queries" "R2DBC" "BusinessCommunication"
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
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "DiscoveryServiceContainer" {
                background #FCA790
                color #000000
                width 300
                height 300
            }
            element "GatewayServiceContainer" {
                background #FCA790
                color #000000
                width 300
                height 300
            }
            element "BusinessContainer" {
                background #78CC5A
                color #000000
                height 250
            }
            element "Database" {
                background #D1D1D1
                color #000000
                shape Cylinder
                height 300
                width 300
            }
            element "Broker" {
                background #F5DC62
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
