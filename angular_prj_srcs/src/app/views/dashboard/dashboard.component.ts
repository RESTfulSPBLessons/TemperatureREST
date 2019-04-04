import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {getStyle, hexToRgba} from '@coreui/coreui/dist/js/coreui-utilities';
import {CustomTooltips} from '@coreui/coreui-plugin-chartjs-custom-tooltips';
import {HttpService} from '../../services/http.service';
import {User} from './user';
import {Temp} from '../../temp';
import {ArduinoStatus} from '../../arduinoStatus';


@Component({
  templateUrl: 'dashboard.component.html',
  providers: [HttpService]
})
export class DashboardComponent implements OnInit {

  radioModel: string = 'Day';
  diagramTitle: string = 'Day statistic'

  // ---------------------------------------------------------------- LOCAL --------------------------------------------------
  localJson = 'assets/data/test.json';
  localWeek = 'assets/data/week.json';
  localMonth = 'assets/data/month.json';
  localToday = 'assets/data/today.json';
  localStatus = 'assets/data/status.json';
  // --------------------------------------------------------------- REMOTE ---------------------------------------------------
  httpGETAll = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/all'; // все записи
  httpGetToday = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/today'; // сегодняшние измерения
  httpGetWeek = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/week'; // дневные за неделю
  httpGetMonthly4Day = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/month'; // месячные, дневная температуры
  httpGetStatus = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/status'; // Статус

  users: User[]; // todo: переименовать
  temps: Temp[]; // todo: переименовать
  arduinoStatus: ArduinoStatus; // Статус
  mainChartData5 = []; // todo: переименовать
  year = []; // время измерения todo: переименовать
  year2 = []; // время измерения todo: переименовать
  count = []; // температура todo: переименовать
  count2 = []; // температура todo: переименовать
  label = []; // метки todo: переименовать
  person = {
    data: [],
    label: 'Temp = '
  };

  mydata = [
    [65, 59, 80, 81, 56, 55, 40],
    [28, 48, 40, 19, 86, 27, 90]
  ];

  public mainChartElements = 5;
  public mainChartData1: Array<number> = [];
  public mainChartData2: Array<number> = [];
  public mainChartData3: Array<number> = [];
  public mainChartData: Array<any> = [this.person, this.person];
  public mainChartLabels: Array<any> = ['Night', 'Morning', 'Day', 'Evening'];
  public mainChartOptions: any = {
    tooltips: {
      enabled: false,
      custom: CustomTooltips,
      intersect: true,
      mode: 'index',
      position: 'nearest',
      callbacks: {
        labelColor: function (tooltipItem, chart) {
          return {backgroundColor: chart.data.datasets[tooltipItem.datasetIndex].borderColor};
        }
      }
    },
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      xAxes: [{
        gridLines: {
          drawOnChartArea: false,
        },
        ticks: {
          callback: function (value: any) {
            return value;
          }
        }
      }],
      yAxes: [{
        ticks: {
          beginAtZero: false,
          maxTicksLimit: 5,
          stepSize: Math.ceil(50 / 5),
          max: 50
        }
      }]
    },
    elements: {
      line: {
        borderWidth: 2
      },
      point: {
        radius: 0,
        hitRadius: 10,
        hoverRadius: 4,
        hoverBorderWidth: 3,
      }
    },
    legend: {
      display: false
    }
  };
  public mainChartColours: Array<any> = [
    { // brandInfo
      backgroundColor: hexToRgba(getStyle('--info'), 10),
      borderColor: getStyle('--info'),
      pointHoverBackgroundColor: '#fff'
    },
    { // brandSuccess
      backgroundColor: 'transparent',
      borderColor: getStyle('--success'),
      pointHoverBackgroundColor: '#fff'
    },
    { // brandDanger
      backgroundColor: 'transparent',
      borderColor: getStyle('--danger'),
      pointHoverBackgroundColor: '#fff',
      borderWidth: 1,
      borderDash: [8, 5]
    }
  ];
  public mainChartLegend = false;
  public mainChartType = 'line';


 /* public brandBoxChartData1: Array<any> = [
    {
      data: [65, 59, 84, 84, 51, 55, 40],
      label: 'Facebook'
    }
  ];
  public brandBoxChartData2: Array<any> = [
    {
      data: [1, 13, 9, 17, 34, 41, 38],
      label: 'Twitter'
    }
  ];
  public brandBoxChartData3: Array<any> = [
    {
      data: [78, 81, 80, 45, 34, 12, 40],
      label: 'LinkedIn'
    }
  ];
  public brandBoxChartData4: Array<any> = [
    {
      data: [35, 23, 56, 22, 97, 23, 64],
      label: 'Google+'
    }
  ];*/

/*  public brandBoxChartLabels: Array<any> = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
  public brandBoxChartOptions: any = {
    tooltips: {
      enabled: false,
      custom: CustomTooltips
    },
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      xAxes: [{
        display: false,
      }],
      yAxes: [{
        display: false,
      }]
    },
    elements: {
      line: {
        borderWidth: 2
      },
      point: {
        radius: 0,
        hitRadius: 10,
        hoverRadius: 4,
        hoverBorderWidth: 3,
      }
    },
    legend: {
      display: false
    }
  };*/
  /*public brandBoxChartColours: Array<any> = [
    {
      backgroundColor: 'rgba(255,255,255,.1)',
      borderColor: 'rgba(255,255,255,.55)',
      pointHoverBackgroundColor: '#fff'
    }
  ];*/
 /* public brandBoxChartLegend = false;
  public brandBoxChartType = 'line';*/

  public random(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }


  dayMode() {

    // Чистим все
    this.count.splice(0, this.count.length);
    this.count2.splice(0, this.count2.length);
    this.year.splice(0, this.year.length);

    console.log(' =============== DAY MODE=================== ');

    this.diagramTitle = 'Today statistic';
    this.httpService.getData4(this.httpGetToday).subscribe(data => {
      this.users = data;
      this.users.forEach(y => {
        this.year.push(y.timeCreated);
        this.count.push(y.temperature);
      });

      console.log(this.count);
      console.log(this.year);

      this.mainChartData = [];
      this.mainChartData = [
        {
          data: this.count,
          label: 'Day'
        },
        {
          data: this.count,
          label: 'Day'
        }
      ];
    });
  }


  ngOnInit() {
    this.dayMode();
    this.getStatus();
    this.label = ['Night', 'Morning', 'Day', 'Evening'];
  }

  public monthMode() {
    console.log(' =============== MONTH MODE=================== ');

    // Чистим все
    this.count.splice(0, this.count.length);
    this.count2.splice(0, this.count2.length);
    this.label.splice(0, this.label.length);
    this.year.splice(0, this.year.length);

    this.diagramTitle = 'Month statistic';
    this.httpService.getData5(this.httpGetMonthly4Day).subscribe(data => {
      this.temps = data;
      console.log(data);

      this.temps.forEach(y => {
        this.year.push(y.measureDate);
        this.label.push((y.measureDate.split('/'))[0] + '/' + (y.measureDate.split('/'))[1]);
        this.count.push(y.dayTemp);
        this.count2.push(y.nightTemp);
      });

      console.log(this.count);
      console.log(this.year);

      this.mainChartData = [];
      this.mainChartData = [
        {
          data: this.count,
          label: 'Day'
        },
        {
          data: this.count2,
          label: 'Night'
        }
      ];
    });
  }

  public weekMode() {
    console.log(' =============== WEEK MODE=================== ');

    // Чистим все
    this.count.splice(0, this.count.length);
    this.count2.splice(0, this.count2.length);
    this.label.splice(0, this.label.length);
    this.year.splice(0, this.year.length);

    this.diagramTitle = 'Week statistic';

    this.httpService.getData5(this.httpGetWeek).subscribe(data => {
      this.temps = data;
      console.log(data);

      this.temps.forEach(y => {
        this.year.push(y.measureDate);
        this.label.push((y.measureDate.split('/'))[0] + '/' + (y.measureDate.split('/'))[1]);
        this.count.push(y.dayTemp);
        this.count2.push(y.nightTemp);
      });

      console.log(this.count);
      console.log(this.year);

      this.mainChartData = [];
      this.mainChartData = [
        {
          data: this.count,
          label: 'Day'
        },
        {
          data: this.count2,
          label: 'Night'
        }
      ];
    });
  }

  public getStatus() {
    console.log(' =============== GET STATUS =================== ');

    this.httpService.getData7(this.httpGetStatus).subscribe(data => {
      this.arduinoStatus = data;
      console.log(data);
    });

    if (this.arduinoStatus == null) {

      console.log('НЕТ СВЯЗИ');

      this.arduinoStatus = new ArduinoStatus();
      this.arduinoStatus.isAcOn = false;
      this.arduinoStatus.isLanOn = false;
      this.arduinoStatus.lastTemperature = 0;
      this.arduinoStatus.serverTime = 'NO CONNECT';
      this.arduinoStatus.lastContactTime = '---';
      this.arduinoStatus.current = 0;
      this.arduinoStatus.amperage = 0;
      this.arduinoStatus.power = 0;
      this.arduinoStatus.consuming = 0;
      this.arduinoStatus.logged = false;
      this.arduinoStatus.lastContactDate = 'NO CONNECT';
    }

  }


  constructor(private httpService: HttpService) {
  }
}
