import { Injectable } from '@angular/core';
import {Entity1} from '../entities/Entity1';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Entity1Service {

  entities: Entity1[] = [
    {
      id: 1,
      name: 'First'
    },
    {
      id: 1,
      name: 'Second'
    },
    {
      id: 3,
      name: 'Third'
    }
  ];

  constructor() { }

 /* getEntities2(_entity2Id: number): Observable<Entity1[]> {

    // return this.entities.find(entity => entity.entity2Id === _entity2Id);
    return this.entities.filter(entity => entity.id === _entity2Id);
  }
*/

  public getEntities(): any {
    const studentsObservable = new Observable(observer => {
      setTimeout(() => {
        observer.next(this.entities);
      }, 1000);
    });

    return studentsObservable;
  }


  public getEntities3(): any {
    return of(this.entities);
  }





}
