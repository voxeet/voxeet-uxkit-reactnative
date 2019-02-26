export class ConferenceUser {
  public externalId: string|undefined;
  public name: string;
  public avatarUrl: string|undefined;

  constructor(externalId: string, name: string, avatarUrl: string|undefined) {
    this.externalId = externalId;
    this.name = name;
    this.avatarUrl = avatarUrl;
  }
};

export class MediaStream {
  public peerId: string;
  public label: string;

  constructor(peerId: string, label: string) {
    this.peerId = peerId;
    this.label = label;
  }
}

export interface ConferenceCreation {
  conferenceId: string;
  metadata: any;
  params: any;
}