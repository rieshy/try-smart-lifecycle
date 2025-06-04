<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Task Queue Manager</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
        }
        .task-form {
            margin-bottom: 20px;
        }
        .task-input {
            width: 70%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-right: 10px;
        }
        .submit-button {
            padding: 8px 16px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .submit-button:hover {
            background-color: #45a049;
        }
        .task-list {
            margin-top: 20px;
        }
        .task-item {
            padding: 10px;
            border: 1px solid #ddd;
            margin-bottom: 10px;
            border-radius: 4px;
            background-color: #fff;
        }
        .status {
            margin-top: 20px;
            padding: 10px;
            background-color: #e8f5e9;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Task Queue Manager</h1>
        
        <div class="task-form">
            <form id="taskForm">
                <input type="text" id="taskDescription" class="task-input" 
                       placeholder="Enter task description" required>
                <button type="submit" class="submit-button">Add Task</button>
            </form>
        </div>

        <div id="status" class="status" style="display: none;"></div>
        
        <div class="task-list" id="taskList">
            <!-- Tasks will be added here dynamically -->
        </div>
    </div>

    <script>
        document.getElementById('taskForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const description = document.getElementById('taskDescription').value;
            const statusDiv = document.getElementById('status');
            
            try {
                const response = await fetch('/try-smart-lifecycle/api/tasks?description=' + encodeURIComponent(description), {
                    method: 'POST'
                });
                
                const result = await response.text();
                
                // Show success message
                statusDiv.textContent = result;
                statusDiv.style.display = 'block';
                statusDiv.style.backgroundColor = '#e8f5e9';
                
                // Clear the input
                document.getElementById('taskDescription').value = '';
                
                // Add task to the list
                const taskList = document.getElementById('taskList');
                const taskItem = document.createElement('div');
                taskItem.className = 'task-item';
                taskItem.textContent = description;
                taskList.insertBefore(taskItem, taskList.firstChild);
                
            } catch (error) {
                statusDiv.textContent = 'Error: ' + error.message;
                statusDiv.style.display = 'block';
                statusDiv.style.backgroundColor = '#ffebee';
            }
        });
    </script>
</body>
</html> 