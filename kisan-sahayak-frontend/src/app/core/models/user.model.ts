// Mirrors com.kisan.user.dto.* on the backend (kisan-user service).

export type UserRole = 'FARMER' | 'EQUIPMENT_OWNER' | 'ADMIN';

// Matches UserDTO.java
export interface UserDTO {
  userId?: string;
  fullName: string;
  phoneNumber: string;
  password?: string; // only sent on register, never rendered back
  villageName: string;
  district: string;
  state: string;
  role: UserRole;
}

// Matches LoginRequestDTO.java
export interface LoginRequestDTO {
  phoneNumber: string;
  password: string;
}

// Matches AuthResponseDTO.java
export interface AuthResponseDTO {
  token: string;
  user: UserDTO;
}
