# try-smart-lifecycle

## Overview
This application demonstrates the use of Spring Smart Lifecycle to implement a robust workflow processing system. The application showcases how to properly manage the lifecycle of background tasks and resources in a Spring application, ensuring graceful startup and shutdown.

## Features
- Implements Spring's SmartLifecycle interface for proper application lifecycle management
- Multi-threaded workflow processing with configurable number of workers
- Support for both Redis and in-memory task queues
- Graceful shutdown handling with configurable timeout
- Web interface for submitting and monitoring workflow tasks
- Comprehensive logging and monitoring capabilities

## Technical Details
- Built with Spring MVC 5.3.31
- Uses Redis for distributed task queue (with fallback to in-memory queue)
- Implements adaptive polling for efficient resource usage
- Provides REST API endpoints for workflow management
- Includes proper resource cleanup during shutdown

## Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- Redis (optional, falls back to in-memory queue if not available)
- Tomcat 9.x

## Configuration
The application can be configured through `application.properties`:
- `app.workflow.worker.count`: Number of worker threads (default: 3)
- `app.workflow.shutdown.timeout.seconds`: Graceful shutdown timeout (default: 30)
- Redis configuration (optional):
  - `redis.host`: Redis host (default: localhost)
  - `redis.port`: Redis port (default: 6379)

## Building and Running
1. Clone the repository
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Deploy to Tomcat:
   ```bash
   ./deploy.sh
   ```
4. Access the application at: http://localhost:8080/try-smart-lifecycle

## Architecture
The application uses a producer-consumer pattern with the following components:
- `WorkflowService`: Main service implementing SmartLifecycle
- `WorkflowWorker`: Worker threads processing tasks
- `WorkflowTaskQueue`: Queue interface with Redis and in-memory implementations
- `WorkflowTask`: Interface for workflow tasks

## Smart Lifecycle Implementation
The application demonstrates proper implementation of Spring's SmartLifecycle interface:
- Automatic startup when Spring context is ready
- Graceful shutdown with configurable timeout
- Proper resource cleanup
- Phase-based startup/shutdown ordering