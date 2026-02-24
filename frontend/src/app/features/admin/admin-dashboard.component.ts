import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="admin-dashboard-container">
      <h1>Admin Dashboard</h1>
      
      <div class="stats-overview">
        <div class="stat-card">
          <h3>System Health</h3>
          <p class="status-ok">Operational</p>
        </div>
        <div class="stat-card">
          <h3>Users Count</h3>
          <p>{{ usersCount }}</p>
        </div>
      </div>

      <div class="admin-sections">
        <section class="admin-section">
          <h2><i class="icon-monitor"></i> Infrastructure Monitoring</h2>
          <p>Access Prometheus and Grafana dashboards for system metrics.</p>
          <div class="links">
            <a href="http://localhost:3000" target="_blank" class="btn btn-secondary">Open Grafana</a>
            <a href="http://localhost:9090" target="_blank" class="btn btn-secondary">Open Prometheus</a>
          </div>
        </section>

        <section class="admin-section">
          <h2><i class="icon-users"></i> User Management</h2>
          <p>Manage users, roles, and access permissions.</p>
          <div class="user-list-preview">
            @if (isLoadingUsers) {
              <div class="loading">Loading users...</div>
            } @else {
              <table class="admin-table">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Role</th>
                  </tr>
                </thead>
                <tbody>
                  @for (user of users; track user) {
                    <tr>
                      <td>{{ user.username }}</td>
                      <td>{{ user.roles ? user.roles.join(', ') : user.role }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            }
          </div>
        </section>
      </div>
    </div>
  `,
  styles: [`
    .admin-dashboard-container { padding: 20px; }
    .stats-overview { display: flex; gap: 20px; margin-bottom: 30px; }
    .stat-card { background: #f8f9fa; border: 1px solid #dee2e6; padding: 20px; border-radius: 8px; flex: 1; text-align: center; }
    .status-ok { color: #28a745; font-weight: bold; }
    .admin-sections { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 30px; }
    .admin-section { background: white; border: 1px solid #e0e0e0; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
    .btn { display: inline-block; padding: 10px 20px; border-radius: 4px; text-decoration: none; margin-right: 10px; font-weight: 500; }
    .btn-secondary { background: #6c757d; color: white; border: 1px solid #6c757d; }
    .admin-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
    .admin-table th, .admin-table td { padding: 12px; text-align: left; border-bottom: 1px solid #eee; }
    .admin-table th { background: #fdfdfd; font-weight: 600; }
  `]
})
export class AdminDashboardComponent implements OnInit {
  private readonly http = inject(HttpClient);

  users: any[] = [];
  usersCount = 0;
  isLoadingUsers = true;

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.http.get<any[]>('/api/admin/users').subscribe({
      next: (data) => {
        this.users = data;
        this.usersCount = data.length;
        this.isLoadingUsers = false;
      },
      error: () => {
        this.isLoadingUsers = false;
        this.users = [];
        this.usersCount = 0;
      }
    });
  }
}
