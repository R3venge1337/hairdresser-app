export interface UserProfileDetailsDto {
  userUuid: string;
  firstname: string;
  surname: string;
  phoneNumber: string;
  username: string;
  password?: string; // Hasło jest opcjonalne lub powinno być pominięte w DTO wysyłanym do klienta
  createdDate: string; // LocalDateTime na backendzie często mapuje się na string w formacie ISO 8601 na froncie
  email: string;
  isActive: boolean;
  name: string; // Nazwa roli
}
