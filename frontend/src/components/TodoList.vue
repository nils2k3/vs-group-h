<template>
  <div class="todo-list">
    <div v-for="todo in todos" :key="todo.id" class="todo-item" :class="{ completed: todo.completed }">
      <div class="todo-header">
        <input
          type="checkbox"
          :checked="todo.completed"
          @change="() => $emit('toggle-todo', todo)"
          class="checkbox"
        />
        <h3 class="todo-title">{{ todo.title }}</h3>
        <button @click="() => $emit('delete-todo', todo.id)" class="btn-delete">🗑️</button>
      </div>
      <p class="todo-description">{{ todo.description }}</p>
      <div class="todo-footer">
        <span class="todo-status" :class="{ done: todo.completed }">
          {{ todo.completed ? '✓ Abgeschlossen' : '⏳ Offen' }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TodoList',
  props: {
    todos: {
      type: Array,
      default: () => []
    }
  },
  emits: ['toggle-todo', 'delete-todo', 'edit-todo']
}
</script>

<style scoped>
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.todo-item {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  border-left: 4px solid #667eea;
}

.todo-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.todo-item.completed {
  background: #f5f5f5;
  opacity: 0.8;
  border-left-color: #4caf50;
}

.todo-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.checkbox {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: #667eea;
}

.todo-title {
  flex: 1;
  margin: 0;
  font-size: 1.1rem;
  color: #333;
  transition: all 0.3s;
}

.todo-item.completed .todo-title {
  text-decoration: line-through;
  color: #999;
}

.btn-delete {
  background: none;
  border: none;
  font-size: 1.3rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.btn-delete:hover {
  opacity: 1;
}

.todo-description {
  color: #666;
  margin: 0 0 1rem 2.5rem;
  line-height: 1.5;
  font-size: 0.95rem;
}

.todo-footer {
  display: flex;
  justify-content: flex-end;
  margin-left: 2.5rem;
}

.todo-status {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  background: #eee;
  color: #666;
}

.todo-status.done {
  background: #e8f5e9;
  color: #4caf50;
}
</style>
