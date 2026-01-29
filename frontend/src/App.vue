<template>
  <div class="app">
    <header class="app-header">
      <h1>📋 ToDo Manager - Gruppe H</h1>
      <p class="subtitle">Gruppe H: Nils Richter, Marc Walter</p>
    </header>

    <main class="app-main">
      <div class="container">
        <!-- Fehler-Anzeige -->
        <div v-if="error" class="error-message">
          <span>❌ {{ error }}</span>
          <button @click="error = null" class="close-btn">×</button>
        </div>

        <!-- Loading-Anzeige -->
        <div v-if="loading" class="loading">
          Loading...
        </div>

        <!-- Erfolgs-Nachricht -->
        <div v-if="successMessage" class="success-message">
          <span>✅ {{ successMessage }}</span>
          <button @click="successMessage = null" class="close-btn">×</button>
        </div>

        <!-- ToDo Formular -->
        <TodoForm @add-todo="addTodo" />

        <!-- ToDo Liste -->
        <TodoList 
          v-if="!loading"
          :todos="todos" 
          @toggle-todo="toggleTodo"
          @delete-todo="deleteTodo"
          @edit-todo="editTodo"
        />

        <!-- Leere Liste Nachricht -->
        <div v-if="!loading && todos.length === 0" class="empty-state">
          <p>Noch keine ToDos. Erstelle eines um zu starten! 🚀</p>
        </div>

        <!-- Statistiken -->
        <div v-if="!loading && todos.length > 0" class="stats">
          <p>Gesamt: {{ todos.length }} | Abgeschlossen: {{ completedCount }} | Offen: {{ pendingCount }}</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import TodoForm from './components/TodoForm.vue'
import TodoList from './components/TodoList.vue'
import { todoApi } from './services/todoApi'

export default {
  name: 'App',
  components: {
    TodoForm,
    TodoList
  },
  setup() {
    const todos = ref([])
    const loading = ref(false)
    const error = ref(null)
    const successMessage = ref(null)

    // Computed Properties
    const completedCount = computed(() => 
      todos.value.filter(todo => todo.completed).length
    )
    const pendingCount = computed(() => 
      todos.value.filter(todo => !todo.completed).length
    )

    // Alle ToDos laden
    const fetchTodos = async () => {
      loading.value = true
      error.value = null
      try {
        const response = await todoApi.getAllTodos()
        todos.value = response.data
      } catch (err) {
        error.value = 'Fehler beim Laden der ToDos: ' + (err.response?.data?.message || err.message)
        console.error('Fetch Error:', err)
      } finally {
        loading.value = false
      }
    }

    // Neues ToDo hinzufügen
    const addTodo = async (todoData) => {
      error.value = null
      loading.value = true
      try {
        const response = await todoApi.createTodo(todoData)
        todos.value.push(response.data)
        successMessage.value = 'ToDo erfolgreich erstellt!'
        setTimeout(() => successMessage.value = null, 3000)
      } catch (err) {
        error.value = 'Fehler beim Erstellen: ' + (err.response?.data?.message || err.message)
        console.error('Create Error:', err)
      } finally {
        loading.value = false
      }
    }

    // ToDo-Status toggeln (completed)
    const toggleTodo = async (todo) => {
      error.value = null
      const originalCompleted = todo.completed
      todo.completed = !todo.completed
      
      try {
        await todoApi.updateTodo(todo.id, {
          title: todo.title,
          description: todo.description,
          completed: todo.completed
        })
        successMessage.value = todo.completed ? 'ToDo abgeschlossen!' : 'ToDo erneut aktiviert!'
        setTimeout(() => successMessage.value = null, 2000)
      } catch (err) {
        todo.completed = originalCompleted
        error.value = 'Fehler beim Aktualisieren: ' + (err.response?.data?.message || err.message)
        console.error('Toggle Error:', err)
      }
    }

    // ToDo löschen
    const deleteTodo = async (id) => {
      error.value = null
      const originalTodos = [...todos.value]
      todos.value = todos.value.filter(todo => todo.id !== id)
      
      try {
        await todoApi.deleteTodo(id)
        successMessage.value = 'ToDo gelöscht!'
        setTimeout(() => successMessage.value = null, 2000)
      } catch (err) {
        todos.value = originalTodos
        error.value = 'Fehler beim Löschen: ' + (err.response?.data?.message || err.message)
        console.error('Delete Error:', err)
      }
    }

    // ToDo bearbeiten
    const editTodo = async (id, updatedData) => {
      error.value = null
      const todoIndex = todos.value.findIndex(t => t.id === id)
      const originalTodo = { ...todos.value[todoIndex] }
      
      todos.value[todoIndex] = { ...todos.value[todoIndex], ...updatedData }
      
      try {
        await todoApi.updateTodo(id, {
          title: updatedData.title,
          description: updatedData.description,
          completed: updatedData.completed
        })
        successMessage.value = 'ToDo aktualisiert!'
        setTimeout(() => successMessage.value = null, 2000)
      } catch (err) {
        todos.value[todoIndex] = originalTodo
        error.value = 'Fehler beim Aktualisieren: ' + (err.response?.data?.message || err.message)
        console.error('Edit Error:', err)
      }
    }

    // Component Mount
    onMounted(() => {
      fetchTodos()
    })

    return {
      todos,
      loading,
      error,
      successMessage,
      completedCount,
      pendingCount,
      addTodo,
      toggleTodo,
      deleteTodo,
      editTodo,
      fetchTodos
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.app-header {
  background: rgba(0, 0, 0, 0.3);
  color: white;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.app-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.subtitle {
  font-size: 0.9rem;
  opacity: 0.9;
}

.app-main {
  padding: 2rem;
}

.container {
  max-width: 800px;
  margin: 0 auto;
}

.error-message,
.success-message {
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-message {
  background-color: #fee;
  color: #c33;
  border: 1px solid #fcc;
}

.success-message {
  background-color: #efe;
  color: #3c3;
  border: 1px solid #cfc;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 1;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: white;
  font-size: 1.1rem;
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  color: #666;
  font-size: 1.1rem;
}

.stats {
  text-align: center;
  margin-top: 2rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: white;
  font-size: 0.95rem;
}
</style>
