export default class Participant {
  
  constructor(
    public participantId: string,
    public conferenceStatus?: string,
    public externalId?: string,
    public name?: string,
    public avatarUrl?: string) {
  }

}
