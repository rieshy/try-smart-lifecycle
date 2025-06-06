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
        .start-workflow-form {
            margin-bottom: 20px;
        }
        .workflow-ids-input {
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
        .workflows-list {
            margin-top: 20px;
        }
        .workflow-item {
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
        <h1>Workflow Manager</h1>
        
        <div class="start-workflow-form">
            <form id="start-workflow-form">
                <input type="text" id="workflow-ids" class="workflow-ids-input" 
                       placeholder="Enter workflow IDs to start (separated by space)" required>
                <button type="submit" class="submit-button">Start Workflows</button>
            </form>
        </div>

        <div id="status" class="status" style="display: none;"></div>
        
        <div class="workflows-list" id="workflows-list">
            <!-- Tasks will be added here dynamically -->
        </div>
    </div>

    <script>
        document.getElementById('start-workflow-form').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const workflowIdsValue = document.getElementById('workflow-ids').value;
            const statusDiv = document.getElementById('status');
            
            try {
                const response = await fetch('/try-smart-lifecycle/api/workflows?workflowIds=' + encodeURIComponent(workflowIdsValue), {
                    method: 'POST'
                });
                
                const result = await response.text();
                
                // Show success message
                statusDiv.textContent = result;
                statusDiv.style.display = 'block';
                statusDiv.style.backgroundColor = '#e8f5e9';
                
                // Clear the input
                document.getElementById('workflow-ids').value = '';
                
                // Add tasks to the list
                const workflowsList = document.getElementById('workflows-list');
                const workflowIds = workflowIdsValue.split(/\s+/).filter(word => word.length > 0);
                
                workflowIds.forEach(workflowId => {
                    const workflowItem = document.createElement('div');
                    workflowItem.className = 'workflow-item';
                    workflowItem.textContent = workflowId;
                    workflowsList.insertBefore(workflowItem, workflowsList.firstChild);
                });
                
            } catch (error) {
                statusDiv.textContent = 'Error: ' + error.message;
                statusDiv.style.display = 'block';
                statusDiv.style.backgroundColor = '#ffebee';
            }
        });
    </script>
</body>
</html> 