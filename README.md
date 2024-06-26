# Task Manager

Logs are stored in the logs folder. I remade the task. I set the main database to PostgreSQL and the secondary one to H2 because I couldn't kill the H2 process to test my auto-reconnect feature. My app is set up for this logic, but in the 'true-task' branch, you can see the code with the opposite configuration. New business logic is added to the project:
1. The maximum number of tasks allowed is 100.
2. When a task is created, it starts with a status of "CREATED" and uses local time by default. We trigger task notifications to Kafka when the deadline approaches (1 hour before, 10 minutes before, and at the deadline itself)

# **How to run**
1. Open your IDE and navigate to the directory where you want to clone the project.
2. Open the command prompt from that directory and enter the following command
   ```git clone https://github.com/Bethakzs/TestTaskPrivatBank.git```
3. Change your current directory to the project directory by entering ```cd TestTaskPrivatBank``` in the command prompt.
4. Ensure Docker Desktop is running on your PC. Then, in the command prompt, enter ```docker-compose up``` to start the project.
5. Open postman and try API request.

# **Technologies**
- Java, Spring (Boot, JDBC), H2, PostgreSQL, Hibernate, Lombok, Swagger, Kafka

# **API Documentation** (also you can check the Swagger UI - http://localhost:8080/swagger-ui/index.html#/)

### **Task Controller**

- **POST** `http://localhost:8080/api/v1/task/create` - Create a new task
    - Request:
    - Request Body, Task:
      ```json
      {
        "title": "Test Task",
        "description": "Description of the task",
        "deadline": "2024-06-25T19:22:59"
      }
      ```
    - Response: 200 OK, Task created with id: {id}
    - 400 Bad Request, Task title cannot be empty
    - 400 Bad Request, Task with title '{title}' already exists
    - 400 Bad Request, Maximum number of tasks reached


- **DELETE** `http://localhost:8080/api/v1/task/delete/{id}` - Delete a task by id
    - Request:
    - PathVariable, ID
    - Response: 200 OK, Task successfully deleted
    - 404 Not Found, Task not found


- **PUT** `http://localhost:8080/api/v1/task/update-status/{id}` - Update the status of a task
    - Request:
    - PathVariable, ID
    - Request Body, status:
      ```json
      {
        "status": "COMPLETED"
      }
        ```
    - Response: 200 OK, Task status updated. New status: {status}
    - 400 Bad Request, Status field is required
    - 404 Not Found, Task not found
    - 400 Bad Request, Invalid status value
     

- **PATCH** `http://localhost:8080/api/v1/task/update/{id}` - Update specific fields of a task
    - PathVariable, ID
    - Request Body, taskDetails:
      ```json
      {
        "title": "New Title",
        "description": "New Description",
        "status": "IN_PROGRESS",
        "deadline": "2024-06-25T19:22:59"
      }
      ```
    - Response: 200 OK, Task successfully updated
    - 400 Bad Request, Title cannot be empty
    - 400 Bad Request, Description cannot be empty
    - 400 Bad Request, Status cannot be empty
    - 400 Bad Request, Invalid status value
    - 400 Bad Request, Deadline cannot be empty
    - 400 Bad Request, Invalid deadline format. Use ISO_LOCAL_DATE_TIME
    - 404 Not Found, Task not found


- **GET** `http://localhost:8080/api/v1/task/get-all` - Retrieve all tasks
    - Response: 200 OK, List of tasks
    - 204 No Content, No tasks found

