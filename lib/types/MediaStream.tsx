export default class MediaStream {
    public peerId: string;
    public label: string;
  
    constructor(peerId: string, label: string) {
      this.peerId = peerId;
      this.label = label;
    }
  }