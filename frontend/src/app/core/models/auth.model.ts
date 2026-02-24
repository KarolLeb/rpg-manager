export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  roles: string[];
  id: number;
}

export interface User {
  id: number;
  username: string;
  roles: string[];
}
