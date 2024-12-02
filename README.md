# Project Description

## Goal
The project demonstrates the integration of modern technologies (MySQL, Hibernate, Redis, Docker) to enable efficient data storage and processing. The primary objective is to build a system that combines a relational MySQL database with Redis caching to optimize access to frequently requested data.

## Technologies
- **MySQL**: Relational database for structured data storage.
- **Hibernate**: ORM framework to simplify database interactions.
- **Redis**: In-memory key-value store for caching with the Least Frequently Used (LFU) eviction strategy.
- **Docker**: Containerization platform for deploying and managing services.

## Functionality
1. Define and map ORM entities for the database schema (Country-City, Language by Country).
2. Implement methods to fetch data from MySQL.
3. Cache frequently requested data in Redis for faster access.
4. Compare the performance of data retrieval from MySQL and Redis.

## Key Features
- Combines relational and in-memory databases for efficient data management.
- Demonstrates the use of Docker for seamless service deployment.
- Explores performance optimization using caching techniques.
