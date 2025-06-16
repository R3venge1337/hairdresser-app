export interface AccessToken {
  accessToken: string;
  refreshToken: string;
}

export interface DecodedToken {
  sub: string; // User ID (UUID) or username, depending on 'sub' claim
  iat: number; // Issued at (Unix timestamp in seconds)
  exp: number; // Expiration time (Unix timestamp in seconds)
  userPhoneNumber: string;
  userUuid: string;
  userType: string; // Rola użytkownika
  accountEnabled: boolean; // Czy konto jest aktywne
  userSurname: string;
  userEmail: string;
  userFirstname: string;
  userLogin: string;
}
