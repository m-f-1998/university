//
//  UpdateDetails.swift
//  F29SO
//

import Foundation
import Eureka
import KeychainAccess

class UpdateDetailsController: FormViewController {
    
    var email: String = ""
    var name: String = ""
    var dob: String = ""
    var mobile: String = ""
    var addressOne: String = ""
    var addressTwo: String = ""
    var city: String = ""
    var zip: String = ""
    var gender: String = ""
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {  // Do any additional setup after loading the view, typically from a nib.
        super.viewDidLoad()
        tableView.isScrollEnabled = false;
        self.navigationController?.navigationBar.topItem?.title = "Update Details"

        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] != nil) {
            email = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["name"] != nil) {
            name = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["name"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["dob"] != nil) {
            dob = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["dob"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["mobile"] != nil) {
            mobile = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["mobile"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["addressone"] != nil) {
            addressOne = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["addressone"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["addresstwo"] != nil) {
            addressTwo = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["addresstwo"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["city"] != nil) {
            city = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["city"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["zip"] != nil) {
            zip = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["zip"] as! String)
        }
        if (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["gender"] != nil) {
            gender = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["gender"] as! String)
        }

        if (gender  == "m") {
            gender = "Male"
        } else {
            gender = "Female"
        }
        
        form +++
            
            //MARK: Details
            
            Section(header: "Update Your Account Details", footer: "Leave Any Fields Blank That You Do Not Wish To Change")
            <<< TextRow(){ row in
                row.title = "E-Mail Address"
                row.placeholder = email
                row.tag = "email"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "Full Name"
                row.placeholder = name
                row.tag = "name"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "Date of Birth"
                row.placeholder = dob
                row.tag = "dob"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "Mobile"
                row.placeholder = mobile
                row.tag = "mobile"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "Address Line One"
                row.placeholder = addressOne
                row.tag = "addressone"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "Address Line Two"
                row.placeholder = addressTwo
                row.tag = "addresstwo"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "City"
                row.placeholder = city
                row.tag = "city"
                row.value = ""
            }
            <<< TextRow(){ row in
                row.title = "Zip Code"
                row.placeholder = zip
                row.tag = "zip"
                row.value = ""
            }
            <<< ActionSheetRow<String>() {
                $0.title = "Gender"
                $0.tag = "gender"
                $0.value = gender
                $0.selectorTitle = "Pick a Gender"
                $0.options = ["Male","Female"]
            }
        
            +++ Section(header: "", footer: "")
            <<< ButtonRow(){
                $0.title = "Submit Changes"
                }.onCellSelection { cell, row in
                    do {
                        let formvalues = self.form.values()
                        var g: String = formvalues["gender"]!! as! String
                        if (g  == "m") {
                            g = "Male"
                        } else {
                            g = "Female"
                        }
                        
                        let password = try Keychain(service:Bundle.main.object(forInfoDictionaryKey: "KaychainGroup") as! String).synchronizable(true).get((((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String))
                        let userIDURL =  "http://www.matthewfrankland.co.uk/pedalPay/userFunctions/updateDetails.php"
                        
                        if (formvalues["email"] != nil) {
                            self.email = formvalues["email"] as! String
                        }
                        
                        if (self.email != "") {
                            if (!self.isValidEmail(testStr: self.email)) {
                                self.alert(title: "Error Updating User Details", message: "Email Address Is Not Valid", style: .alert)
                                return
                            }
                        }
                        
                        if (formvalues["name"] != nil) {
                            self.name = formvalues["name"] as! String
                        }
                        
                        if (formvalues["dob"] != nil) {
                            self.dob = formvalues["dob"] as! String
                        }
                        
                        if (self.dob != "") {
                            if (!self.isValidDate(testStr: self.dob)) {
                                self.alert(title: "Error Updating Date Of Birth", message: "Date Must Be In The Format yyyy-MM-dd (With Dashes)", style: .alert)
                                return
                            }
                        }
                        
                        if (formvalues["addressone"] != nil) {
                            self.addressOne = formvalues["addressone"] as! String
                        }
                        
                        if (formvalues["addresstwo"] != nil) {
                            self.addressTwo = formvalues["addresstwo"] as! String
                        }
                        
                        if (formvalues["city"] != nil) {
                            self.city = formvalues["city"] as! String
                        }
                        
                        if (formvalues["zip"] != nil) {
                            self.zip = formvalues["zip"] as! String
                        }
                        
                        if (formvalues["gender"] != nil) {
                            self.gender = formvalues["gender"] as! String
                        }
                        
                        if (formvalues["mobile"] != nil) {
                            self.mobile = formvalues["mobile"] as! String
                        }
                        
                        if (self.mobile != "") {
                            if (!self.isValidNumber(testStr: self.email)) {
                                self.alert(title: "Error Updating User Details", message: "Mobile Number Is Not Valid", style: .alert)
                                return
                            }
                        }
                        
                        self.sendRequest(userIDURL, method: .post, parameters: [
                            "oldEmail": (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String),
                            "password": password!,
                            "name": self.name,
                            "dob": self.dob,
                            "addressOne": self.addressOne,
                            "addressTwo": self.addressTwo,
                            "city": self.city,
                            "zip": self.zip,
                            "gender": self.gender,
                            "mobile": self.mobile,
                            "newEmail": self.email
                            ], completionHandler: { (data, response, error) in
                            guard let data = data else { return }
                            do {
                                let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String : Any]
                                let returnMessage = json["message"] as! String
                                let errorReturn = json["error"] as! Bool
                                DispatchQueue.main.async {
                                    self.navigationController?.popViewController(animated: true)
                                    self.alert(title: "Updating Details Request", message: returnMessage, style: .alert)
                                    if (errorReturn == false) {
                                        let loginURL: String = "http://www.matthewfrankland.co.uk/pedalPay/login/login.php"
                                        if (self.email == "") {
                                            self.email = (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String)
                                        }
                                        self.sendRequest(loginURL, method: .post, parameters: ["email": self.email, "password": password!], completionHandler: { (data, response, error) in
                                            guard let data = data else { return }
                                            do {
                                                let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String : Any]
                                                DispatchQueue.main.async {
                                                    let user: [[String: Any]] = json["userDetails"] as! [[String: Any]]
                                                    UserDefaults.standard.setValue(user, forKey: "userDetails")
                                                    do {
                                                        let email = user[0]["email"] as! String
                                                        try Keychain(service: Bundle.main.object(forInfoDictionaryKey: "KaychainGroup") as! String).synchronizable(true).set(password!, key: email)
                                                    } catch {
                                                        fatalError("Error updating keychain - \(error)")
                                                    }
                                                }
                                            } catch _ as NSError {}
                                        })?.resume()
                                    }
                                }
                            } catch let error as NSError {
                                self.alert(title: "Fatal Error In Updating Account Details", message: error.description, style: UIAlertController.Style.alert)
                            }
                        })?.resume()
                    } catch {
                        fatalError("Error fetching password items - \(error)")
                    }
                }

    }
    
    private func isValidEmail(testStr:String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: testStr)
    }
    
    private func isValidNumber(testStr:String) -> Bool {
        let num = Int(testStr)
        if num != nil {
            return true
        }
        else {
            return false
        }
    }
    
    private func isValidDate(testStr:String) -> Bool {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        
        if dateFormatter.date(from:testStr) != nil {
            let date = dateFormatter.date(from:testStr)!
            let today = Date()
            if (date < today) {
                return true
            }
        }
        return false
    }
    
}
