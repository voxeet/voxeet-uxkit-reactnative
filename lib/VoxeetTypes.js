export class ConferenceUser {
  externalId: string;
  name: string;
  avatarUrl: string;

  constructor(externalId, name, avatarUrl) {
    this.externalId = externalId;
    this.name = name;
    this.avatarUrl = avatarUrl;
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
