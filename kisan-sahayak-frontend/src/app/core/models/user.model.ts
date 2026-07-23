export type UserRole = 'FARMER' | 'OWNER' | 'ADMIN';

export interface UserDTO {
  userId?: string;
  fullName: string;
  phoneNumber: string;
  password?: string;
  villageName: string;
  district: string;
  state: string;
  roles?: UserRole[];
}

export interface LoginRequestDTO {
  phoneNumber: string;
  password: string;
}

export interface AuthResponseDTO {
  token: string;
  user: UserDTO;
}
