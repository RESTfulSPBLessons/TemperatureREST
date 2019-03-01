import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Request} from '../request';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Response} from '@angular/http';
import {Premium} from '../premium';


@Injectable()
export class HttpService {

  constructor(private http: HttpClient) { }

  postData(req: Request) {
    const body = {calcID: req.calcID, compareType: req.compareType};
    console.log(req.calcID);
    return this.http.post('http://localhost:8083/RCCT-2.0-SNAPSHOT/rest/get', body);
  }

  public getData(url: string): Observable<any> {
    return this.http.get(url);
  }


  public getData2(url: string) {
    return this.http.get(url);
  }

  public getData4(url: string) {
    console.log('GET DATA......');
    console.log(url);
    return this.http.get(url);
  }

  public getData5(url: string) {
    console.log('GET DATA......');
    console.log(url);
    return this.http.get(url).pipe(map(data => {
      let usersList = data['userList'];
      return usersList.map(user => user.age);
    }));
  }



  // 'assets/data/test.json'



}
