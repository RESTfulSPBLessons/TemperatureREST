export class ArduinoStatus {
  public who: string;
  public isAcOn: boolean;
  public isLanOn: boolean;
  public lastTemperature: number;
  public lastHumidity: number;
  public serverTime: string;
  public lastContactTime: string;
  public current: number;
  public amperage: number;
  public power: number;
  public consuming: number;
  public logged: boolean;
  public lastContactDate: string;

  get getAc(): boolean {
    return this.isAcOn;
  }

  constructor() {
  }

}
