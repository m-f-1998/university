//
//  CurrentBookings.php
//  F29SO
//

import UIKit
import KeychainAccess

class CurrentBookingsController: UITableViewController {
    
    var bookings: [[String : Any]] = [[:]]
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.topItem?.title = "Account Bookings"
        self.tableView.delegate = self
        self.tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        requestBookings()
    }
    
    private func requestBookings() {
        let bookingsURL = "http://www.matthewfrankland.co.uk/pedalPay/userFunctions/getBookings.php"
        do {
            let password = try Keychain(service:Bundle.main.object(forInfoDictionaryKey: "KaychainGroup") as! String).synchronizable(true).get((((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String))
            self.sendRequest(bookingsURL, method: .post, parameters: ["email": (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String), "password": password!], completionHandler: { (data, response, error) in
                guard let data = data else { return }
                do {
                    let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String : Any]
                    let returnMessage = json["message"] as! [[String : Any]]
                    DispatchQueue.main.async {
                        if (returnMessage.isEmpty) {
                            self.navigationController?.popViewController(animated: true)
                            self.alert(title: "No Bookings Found", message: "", style: .alert)
                        } else {
                            self.bookings = returnMessage
                            self.tableView.reloadData()
                        }
                    }
                } catch let error as NSError {
                    self.alert(title: "Fatal Error In Requesting Bookings", message: error.description, style: UIAlertController.Style.alert)
                }
            })?.resume()
        } catch {
            fatalError("Error fetching password items - \(error)")
        }
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return bookings.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        cell.textLabel?.numberOfLines = 10
        cell.textLabel!.lineBreakMode = .byWordWrapping
        if (bookings[indexPath.row]["id"] != nil) {
            let bookingID = "Booking ID: " + (bookings[indexPath.row]["id"] as! NSNumber).stringValue
            let bikeID = "\nBike ID: " + (bookings[indexPath.row]["bid"] as! NSNumber).stringValue
            var locationID = (bookings[indexPath.row]["lid"] as! NSNumber).stringValue
            var flag = false
            for i in 0...(UserDefaults.standard.value(forKey: "locations") as! NSArray).count-1 {
                let loc = (UserDefaults.standard.value(forKey: "locations") as! NSArray)[i] as! NSDictionary
                if (loc["id"] as! String == locationID) {
                    flag = true
                    locationID = loc["hub"] as! String
                }
            }
            if (!flag) {
                locationID = "Unknown"
            }
            let location = "\nLocation: " + locationID
            let startTime = "\nStart Of Booking: " + (bookings[indexPath.row]["bookstart"] as! String)
            let endTime = "\nEnd Of Booking: " + (bookings[indexPath.row]["bookend"] as! String)
            var returned = "No"
            if ((bookings[indexPath.row]["processing"] as! NSNumber).stringValue == "1") {
                returned = "Yes"
            }
            let bikeReturned = "\nBike Returned? " + returned
            let distance = bookings[indexPath.row]["distance"] as! NSNumber
            let distanceTraveled = "\nDistance Travelled: " + distance.stringValue + " Miles"
            let caloriesBurned = "\nCallories Burned: " + String(distance.intValue * 52)
            cell.textLabel!.text = bookingID + bikeID + location + startTime + endTime + bikeReturned + distanceTraveled + caloriesBurned
        }
        return cell
    }

}
