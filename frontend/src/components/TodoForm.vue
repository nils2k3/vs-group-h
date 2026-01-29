<template>
  <form @submit.prevent="handleSubmit" class="todo-form">
    <div class="form-group">
      <input
        v-model="form.title"
        type="text"
        placeholder="Titel eingeben..."
        class="form-input title-input"
        required
      />
    </div>

    <div class="form-group">
      <textarea
        v-model="form.description"
        placeholder="Beschreibung eingeben..."
        class="form-input description-input"
        rows="3"
        required
      ></textarea>
    </div>

    <button type="submit" class="btn-primary" :disabled="submitting">
      {{ submitting ? 'Wird erstellt...' : '✚ Hinzufügen' }}
    </button>
  </form>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'TodoForm',
  emits: ['add-todo'],
  setup(props, { emit }) {
    const form = ref({
      title: '',
      description: ''
    })
    const submitting = ref(false)

    const handleSubmit = async () => {
      if (!form.value.title.trim() || !form.value.description.trim()) {
        alert('Titel und Beschreibung sind erforderlich!')
        return
      }

      submitting.value = true
      emit('add-todo', {
        title: form.value.title,
        description: form.value.description,
        completed: false
      })

      // Form zurücksetzen
      form.value = {
        title: '',
        description: ''
      }
      submitting.value = false
    }

    return {
      form,
      submitting,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.todo-form {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-family: inherit;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.title-input {
  font-weight: 600;
}

.description-input {
  resize: vertical;
}

.btn-primary {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
