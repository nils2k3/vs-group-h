import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// Axios instance mit Standard-Config
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const todoApi = {
  /**
   * Alle ToDos abrufen
   * GET /todos
   */
  getAllTodos() {
    return apiClient.get('/todos')
  },

  /**
   * Einzelnes ToDo abrufen
   * GET /todos/{id}
   */
  getTodoById(id) {
    return apiClient.get(`/todos/${id}`)
  },

  /**
   * Neues ToDo erstellen
   * POST /todos
   */
  createTodo(todoData) {
    return apiClient.post('/todos', {
      title: todoData.title,
      description: todoData.description,
      completed: todoData.completed || false
    })
  },

  /**
   * ToDo aktualisieren
   * PUT /todos/{id}
   */
  updateTodo(id, todoData) {
    return apiClient.put(`/todos/${id}`, {
      title: todoData.title,
      description: todoData.description,
      completed: todoData.completed
    })
  },

  /**
   * ToDo löschen
   * DELETE /todos/{id}
   */
  deleteTodo(id) {
    return apiClient.delete(`/todos/${id}`)
  }
}

export default todoApi
