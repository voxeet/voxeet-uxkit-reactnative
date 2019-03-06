export enum UserType {
  USER = "user",
  LISTENER = "listener"
}
  
export interface JoinUserInfo {
  type?:  UserType;
}
  
export default interface JoinOptions {
  user?: JoinUserInfo;
}