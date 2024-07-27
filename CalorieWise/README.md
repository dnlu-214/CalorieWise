# Clean-Desktop-UI
CS 346 sample, demonstrating MVVM and clean architecture.

* **Dependencies**: flow from View (top) to Model (bottom).
* **Data flow**: notifications flow bottom to top via interfaces.

```mermaid
classDiagram
    View "1" ..> "1" UserController
    UserController "*" ..> "1" UserModel 
    
    ISubscriber "1" <|.. "1" UserViewModel
    IPublisher <|.. UserModel
    ISubscriber "*" <.. "*" IPublisher

    View "1" <-- "1" UserViewModel 
    UserViewModel "*" <-- "*" UserModel
    
    class View {
        -UserController viewmodel    
        -UserViewModel viewModel
    }
    
    class ISubscriber {
        <<Interface>>
        +update()    
    }  
    
    class UserViewModel {
        -View view
        -UserModel model
        +update()
    }
    
    class UserController {
        +UserModel model
        +invoke(Event)
    }

    class IPublisher {
        <<Interface>>
        -List~Subscriber~ subscribers
        +notify()
    }
    
    class UserModel {
        -String firstname
        -String lastname
        +subscribe(ISubscriber)
        +unsubscribe(ISubscriber)
    }

```