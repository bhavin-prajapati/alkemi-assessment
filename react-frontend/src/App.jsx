import { useState, useEffect } from 'react'
import './App.css'
import { getUsers, createUser, createTask, getUserById, getTasks, getStats, checkHealth, updateTask } from './services/api'
import UserList from './components/UserList'
import TaskList from './components/TaskList'
import Stats from './components/Stats'
import HealthStatus from './components/HealthStatus'

function App() {
  const [users, setUsers] = useState([])
  const [tasks, setTasks] = useState([])
  const [stats, setStats] = useState(null)
  const [health, setHealth] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [selectedUserId, setSelectedUserId] = useState(null)
  const [selectedUser, setSelectedUser] = useState(null)
  const [taskFilter, setTaskFilter] = useState('')
  const [newUserName, setNewUserName] = useState('')
  const [newUserEmail, setNewUserEmail] = useState('')
  const [newUserRole, setNewUserRole] = useState('')
  const [newTaskTitle, setNewTaskTitle] = useState('')
  const [newTaskStatus, setNewTaskStatus] = useState('pending')
  const [newTaskUserId, setNewTaskUserId] = useState('')
  const [selectedTask, setSelectedTask] = useState(null)

  useEffect(() => {
    loadInitialData()
  }, [])

  const loadInitialData = async () => {
    setLoading(true)
    setError(null)
    try {
      // Check health first
      const healthData = await checkHealth()
      setHealth(healthData)

      // Load data in parallel
      const [usersData, tasksData, statsData] = await Promise.all([
        getUsers(),
        getTasks(),
        getStats()
      ])

      setUsers(usersData.users || [])
      setTasks(tasksData.tasks || [])
      setStats(statsData)
    } catch (err) {
      setError(err.message || 'Failed to load data')
      console.error('Error loading data:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleUserSelect = async (userId) => {
    setSelectedUserId(userId)
    setLoading(true)
    setError(null)
    try {
      const user = await getUserById(userId)
      setSelectedUser(user)
      // Also filter tasks for this user
      const userTasks = await getTasks('', userId.toString())
      setTasks(userTasks.tasks || [])
    } catch (err) {
      setError(err.message || 'Failed to load user details')
      console.error('Error loading user:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleTaskFilter = async (status) => {
    setTaskFilter(status)
    setLoading(true)
    setError(null)
    try {
      const tasksData = await getTasks(status, '')
      setTasks(tasksData.tasks || [])
    } catch (err) {
      setError(err.message || 'Failed to filter tasks')
      console.error('Error filtering tasks:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleCreateUser = async () => {
    if (!newUserName.trim() || !newUserEmail.trim() || !newUserRole.trim()) {
      setError('Name, email, and role are required to create a user.')
      return
    }

    setLoading(true)
    setError(null)
    try {
      const createdUser = await createUser({
        name: newUserName.trim(),
        email: newUserEmail.trim(),
        role: newUserRole.trim(),
      })
      setUsers((prevUsers) => [createdUser, ...prevUsers])
      setNewUserName('')
      setNewUserEmail('')
      setNewUserRole('')
    } catch (err) {
      setError(err.message || 'Failed to create user')
      console.error('Error creating user:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleCreateTask = async () => {
    if (!newTaskTitle.trim() || !newTaskStatus.trim() || !newTaskUserId.trim()) {
      setError('Title, status, and user ID are required to create a task.')
      return
    }

    const userIdNumber = Number(newTaskUserId)
    if (!userIdNumber || userIdNumber <= 0) {
      setError('User ID must be a positive number.')
      return
    }

    setLoading(true)
    setError(null)
    try {
      const createdTask = await createTask({
        title: newTaskTitle.trim(),
        status: newTaskStatus.trim(),
        userId: userIdNumber,
      })
      setTasks((prevTasks) => [createdTask, ...prevTasks])
      setNewTaskTitle('')
      setNewTaskStatus('pending')
      setNewTaskUserId('')
    } catch (err) {
      setError(err.message || 'Failed to create task')
      console.error('Error creating task:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleTaskSelect = (task) => {
    setSelectedTask(task)
    setNewTaskTitle(task.title || '')
    setNewTaskStatus(task.status || 'pending')
    // Support multiple shapes: { userId }, { user: { id } }, or { user: { userId } }
    const rawUserId = task.userId ?? task.user?.id ?? task.user?.userId ?? ''
    setNewTaskUserId(rawUserId !== undefined && rawUserId !== null ? String(rawUserId) : '')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const handleUpdateTask = async () => {
    if (!selectedTask) return
    if (!newTaskTitle.trim() || !newTaskStatus.trim() || !newTaskUserId.trim()) {
      setError('Title, status, and user ID are required to update a task.')
      return
    }

    const userIdNumber = Number(newTaskUserId)
    if (!userIdNumber || userIdNumber <= 0) {
      setError('User ID must be a positive number.')
      return
    }

    setLoading(true)
    setError(null)
    try {
      const updated = await updateTask(selectedTask.id, {
        title: newTaskTitle.trim(),
        status: newTaskStatus.trim(),
        userId: userIdNumber,
      })
      setTasks((prev) => prev.map((t) => (t.id === updated.id ? updated : t)))
      // clear form
      setSelectedTask(null)
      setNewTaskTitle('')
      setNewTaskStatus('pending')
      setNewTaskUserId('')
    } catch (err) {
      setError(err.message || 'Failed to update task')
      console.error('Error updating task:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleRefresh = () => {
    setSelectedUserId(null)
    setSelectedUser(null)
    setTaskFilter('')
    loadInitialData()
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Java Developer Test Project</h1>
        <p>React Frontend → Node.js Backend → Java Backend</p>
      </header>

      <HealthStatus health={health} />

      {error && (
        <div className="error-banner">
          <span>⚠️ {error}</span>
          <button onClick={handleRefresh}>Retry</button>
        </div>
      )}

      <div className="main-content">
        <div className="stats-section">
          {stats && <Stats stats={stats} />}
        </div>

        <div className="data-section">
          <div className="panel">
            <h2>Users</h2>
            <div className="create-user-section">
              <h3>Create New User</h3>
              <div className="create-user-form">
                <input
                  type="text"
                  placeholder="Name"
                  value={newUserName}
                  onChange={(e) => setNewUserName(e.target.value)}
                />
                <input
                  type="email"
                  placeholder="Email"
                  value={newUserEmail}
                  onChange={(e) => setNewUserEmail(e.target.value)}
                />
                <input
                  type="text"
                  placeholder="Role"
                  value={newUserRole}
                  onChange={(e) => setNewUserRole(e.target.value)}
                />
                <button className="create-btn" onClick={handleCreateUser}>
                  Create
                </button>
              </div>
            </div>
            {loading && !users.length ? (
              <div className="loading">Loading users...</div>
            ) : (
              <UserList
                users={users}
                selectedUserId={selectedUserId}
                onUserSelect={handleUserSelect}
              />
            )}
            {selectedUser && (
              <div className="user-details">
                <h3>Selected User Details</h3>
                <div className="detail-card">
                  <p><strong>Name:</strong> {selectedUser.name}</p>
                  <p><strong>Email:</strong> {selectedUser.email}</p>
                  <p><strong>Role:</strong> {selectedUser.role}</p>
                </div>
              </div>
            )}
          </div>

          <div className="panel">
            <div className="panel-header">
              <h2>Tasks</h2>
              <div className="filter-buttons">
                <button
                  className={taskFilter === '' ? 'active' : ''}
                  onClick={() => handleTaskFilter('')}
                >
                  All
                </button>
                <button
                  className={taskFilter === 'pending' ? 'active' : ''}
                  onClick={() => handleTaskFilter('pending')}
                >
                  Pending
                </button>
                <button
                  className={taskFilter === 'in-progress' ? 'active' : ''}
                  onClick={() => handleTaskFilter('in-progress')}
                >
                  In Progress
                </button>
                <button
                  className={taskFilter === 'completed' ? 'active' : ''}
                  onClick={() => handleTaskFilter('completed')}
                >
                  Completed
                </button>
              </div>
            </div>
            <div className="create-task-section">
              <h3>Create New Task</h3>
              <div className="create-task-form">
                <input
                  type="text"
                  placeholder="Title"
                  value={newTaskTitle}
                  onChange={(e) => setNewTaskTitle(e.target.value)}
                />
                <select
                  value={newTaskStatus}
                  onChange={(e) => setNewTaskStatus(e.target.value)}
                >
                  <option value="pending">Pending</option>
                  <option value="in-progress">In Progress</option>
                  <option value="completed">Completed</option>
                </select>
                <input
                  type="number"
                  placeholder="User ID"
                  value={newTaskUserId}
                  onChange={(e) => setNewTaskUserId(e.target.value)}
                />
                <button
                  className="create-btn"
                  onClick={selectedTask ? handleUpdateTask : handleCreateTask}
                >
                  {selectedTask ? 'Update' : 'Create'}
                </button>
              </div>
            </div>
            {loading && !tasks.length ? (
              <div className="loading">Loading tasks...</div>
            ) : (
              <TaskList tasks={tasks} onTaskSelect={handleTaskSelect} />
            )}
          </div>
        </div>
      </div>

      <footer className="app-footer">
        <button onClick={handleRefresh} className="refresh-btn">
          🔄 Refresh All Data
        </button>
      </footer>
    </div>
  )
}

export default App
