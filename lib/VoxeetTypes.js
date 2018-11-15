export class ConferenceUser {
  externalId: string;
  name: string;
  avatar: string;

  constructor(externalId, name, avatar) {
    this.externalId = externalId;
    this.name = name;
    this.avatar = avatar;
  }
};

export class MediaStream {
  peerId: string;
  label: string;

  constructor(peerId, label) {
    this.peerId = peerId;
    this.label = label;
  }
}
