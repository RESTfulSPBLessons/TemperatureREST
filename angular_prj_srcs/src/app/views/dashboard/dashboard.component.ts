import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {getStyle, hexToRgba} from '@coreui/coreui/dist/js/coreui-utilities';
import {CustomTooltips} from '@coreui/coreui-plugin-chartjs-custom-tooltips';
import {HttpService} from '../../services/http.service';
// import {Premium} from '../../premium';
// import {Book} from '../base/Book';
import {User} from './user';
import {Temp} from '../../temp';

// import {HttpClient} from '@angular/common/http';


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
  // --------------------------------------------------------------- REMOTE ---------------------------------------------------
  httpGETAll = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/all'; // все записи
  httpGetToday = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/today'; // сегодняшние измерения
  httpGetWeekly4Day = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/weekday'; // дневные за неделю
  httpGetWeekly4Night = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/monthnight'; // ночные за неделю
  httpGetMonthly4Day = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/monthday'; // месячные, дневная температуры
  httpGetMonthly4Night = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/monthnight'; // месячные, ночная температуры
  httpGetStatus = 'http://localhost:8080/RCCT-2.0-SNAPSHOT/rest/users/status'; // Статус

  users: User[]; // todo: переименовать
  temps: Temp[]; // todo: переименовать
  mainChartData5 = []; // todo: переименовать

  year = []; // время измерения todo: переименовать
  year2 = []; // время измерения todo: переименовать
  // label = []; // подпись метки
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

  /*

    // lineChart1
    public lineChart1Data: Array<any> = [
      {
        data: [65, 59, 84, 84, 51, 55, 40],
        label: 'Series A'
      }
    ];
    public lineChart1Labels: Array<any> = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
    public lineChart1Options: any = {
      tooltips: {
        enabled: false,
        custom: CustomTooltips
      },
      maintainAspectRatio: false,
      scales: {
        xAxes: [{
          gridLines: {
            color: 'transparent',
            zeroLineColor: 'transparent'
          },
          ticks: {
            fontSize: 2,
            fontColor: 'transparent',
          }

        }],
        yAxes: [{
          display: false,
          ticks: {
            display: false,
            min: 40 - 5,
            max: 84 + 5,
          }
        }],
      },
      elements: {
        line: {
          borderWidth: 1
        },
        point: {
          radius: 4,
          hitRadius: 10,
          hoverRadius: 4,
        },
      },
      legend: {
        display: false
      }
    };
    public lineChart1Colours: Array<any> = [
      {
        backgroundColor: getStyle('--primary'),
        borderColor: 'rgba(255,255,255,.55)'
      }
    ];
    public lineChart1Legend = false;
    public lineChart1Type = 'line';

  //  public premiumsO: Premium[] = [];

    // lineChart2
    public lineChart2Data: Array<any> = [
      {
        data: [1, 18, 9, 17, 34, 22, 11],
        label: 'Series A'
      }
    ];
    public lineChart2Labels: Array<any> = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
    public lineChart2Options: any = {
      tooltips: {
        enabled: false,
        custom: CustomTooltips
      },
      maintainAspectRatio: false,
      scales: {
        xAxes: [{
          gridLines: {
            color: 'transparent',
            zeroLineColor: 'transparent'
          },
          ticks: {
            fontSize: 2,
            fontColor: 'transparent',
          }

        }],
        yAxes: [{
          display: false,
          ticks: {
            display: false,
            min: 1 - 5,
            max: 34 + 5,
          }
        }],
      },
      elements: {
        line: {
          tension: 0.00001,
          borderWidth: 1
        },
        point: {
          radius: 4,
          hitRadius: 10,
          hoverRadius: 4,
        },
      },
      legend: {
        display: false
      }
    };
    public lineChart2Colours: Array<any> = [
      { // grey
        backgroundColor: getStyle('--info'),
        borderColor: 'rgba(255,255,255,.55)'
      }
    ];
    public lineChart2Legend = false;
    public lineChart2Type = 'line';


    // lineChart3
    public lineChart3Data: Array<any> = [
      {
        data: [78, 81, 80, 45, 34, 12, 40],
        label: 'Series A'
      }
    ];
    public lineChart3Labels: Array<any> = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
    public lineChart3Options: any = {
      tooltips: {
        enabled: false,
        custom: CustomTooltips
      },
      maintainAspectRatio: false,
      scales: {
        xAxes: [{
          display: false
        }],
        yAxes: [{
          display: false
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
        },
      },
      legend: {
        display: false
      }
    };
    public lineChart3Colours: Array<any> = [
      {
        backgroundColor: 'rgba(255,255,255,.2)',
        borderColor: 'rgba(255,255,255,.55)',
      }
    ];
    public lineChart3Legend = false;
    public lineChart3Type = 'line';


    // barChart1
    public barChart1Data: Array<any> = [
      {
        data: [78, 81, 80, 45, 34, 12, 40, 78, 81, 80, 45, 34, 12, 40, 12, 40],
        label: 'Series A'
      }
    ];
    public barChart1Labels: Array<any> = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16'];
    public barChart1Options: any = {
      tooltips: {
        enabled: false,
        custom: CustomTooltips
      },
      maintainAspectRatio: false,
      scales: {
        xAxes: [{
          display: false,
          barPercentage: 0.6,
        }],
        yAxes: [{
          display: false
        }]
      },
      legend: {
        display: false
      }
    };
    public barChart1Colours: Array<any> = [
      {
        backgroundColor: 'rgba(255,255,255,.3)',
        borderWidth: 0
      }
    ];
    public barChart1Legend = false;
    public barChart1Type = 'bar';
  */

  // mainChart

  public mainChartElements = 5;
  public mainChartData1: Array<number> = [];
  public mainChartData2: Array<number> = [];
  public mainChartData3: Array<number> = [];

// {data: this.count, label: 'Температура'}


  public mainChartData: Array<any> = [this.person, this.person];

  /*public mainChartData: Array<any> = [
    {
      data: this.mainChartData1,
      label: 'Current'
    },
    {
      data: this.mainChartData2,
      label: 'Previous'
    },
    {
      data: this.mainChartData3,
      label: 'BEP'
    }
  ];*/
  /* tslint:disable:max-line-length */
  public mainChartLabels: Array<any> = ['Night', 'Morning', 'Day', 'Evening'];
  /* tslint:enable:max-line-length */
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

  // social box charts

  public brandBoxChartData1: Array<any> = [
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
  ];

  public brandBoxChartLabels: Array<any> = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
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
  };
  public brandBoxChartColours: Array<any> = [
    {
      backgroundColor: 'rgba(255,255,255,.1)',
      borderColor: 'rgba(255,255,255,.55)',
      pointHoverBackgroundColor: '#fff'
    }
  ];
  public brandBoxChartLegend = false;
  public brandBoxChartType = 'line';

  public random(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }

  // constructor(private http: HttpClient){}

  clickEvent() {
  }


  dayMode() {

    // Чистим все
    // this.label.splice(0, this.label.length);
    this.count.splice(0, this.count.length);
    this.count2.splice(0, this.count2.length);
    this.year.splice(0, this.year.length);

    console.log(' =============== DAY MODE=================== ');

    this.diagramTitle = 'Today statistic';
    this.httpService.getData4(this.localToday).subscribe(data => {
      this.users = data;
      //  console.log(data);
      this.users.forEach(y => {
        this.year.push(y.timeCreated);
        //  this.label.push(y.timeCreated);
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
    this.label = ['Night', 'Morning', 'Day', 'Evening'];
  }

  public monthMode() {
    console.log(' =============== MONTH MODE=================== ');

    // Чистим все
    // this.label.splice(0, this.label.length);
    this.count.splice(0, this.count.length);
    this.count2.splice(0, this.count2.length);
    this.label.splice(0, this.label.length);
    this.year.splice(0, this.year.length);

    this.diagramTitle = 'Month statistic';
    this.httpService.getData5(this.localMonth).subscribe(data => {
      this.temps = data;
      console.log(data);

      this.temps.forEach(y => {
        this.year.push(y.measureDate);
        this.label.push((y.measureDate.split('/'))[0] + '/' + (y.measureDate.split('/'))[1]);
       // console.log((y.measureDate.split('/'))[0] + '/' + (y.measureDate.split('/'))[1]);
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

    this.httpService.getData5(this.localWeek).subscribe(data => {
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


  constructor(private httpService: HttpService) {
  }
}