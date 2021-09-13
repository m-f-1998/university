//
//  MainMapController.swift
//  F29SO
//

import UIKit.UIViewController
import MapKit
import KeychainAccess

class MainMapController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    
    private var mapView: MKMapView?
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() { // Do any additional setup after loading the view, typically from a nib.
        setupMapKit() // Initial mapView setup
        self.mapView?.delegate = self
    }
    
    override func viewDidAppear(_ animated: Bool) { //Start new threads that will take a long time to execut
        super.viewDidAppear(true)
        if (mapView?.annotations.count)! > 0 {
            for i in 0...(mapView?.annotations.count)! - 1 {  // Deselect all annotations in case trying to hire bike
                let annotation = mapView?.annotations[i]
                mapView?.deselectAnnotation(annotation!, animated: true)
            }
        }
    }
    
    //MARK: - MapView Delegate
    
    func mapView(_ mapView: MKMapView, didSelect view: MKAnnotationView) { // Location selected for hiring bike
        let hireController = HireBikeController() as HireBikeController
        hireController.selectedLocation = (view.annotation!.title)!!
        self.navigationController?.pushViewController(hireController, animated: true)
    }
    
    //MARK: - Setup Functions
    
    private func setupMapKit() { //Initial setup of map upon logging in
        self.mapView = MKMapView(frame: CGRect.init(x: 0, y: 0, width: (UIApplication.shared.keyWindow?.bounds.width)!, height: (UIApplication.shared.keyWindow?.bounds.height)!))
        setRegion(lat: CLLocationDegrees.init(55.9533), long: CLLocationDegrees.init(-3.198267))
        self.view.addSubview(self.mapView!)
    }
    
    private func setRegion(lat: CLLocationDegrees, long: CLLocationDegrees) { // Set hover region to region of use
        getAnnotations()
        let viewRegion = MKCoordinateRegion(center: CLLocationCoordinate2D.init(latitude: lat, longitude: long), latitudinalMeters: 15500, longitudinalMeters: 15500)
        mapView?.setRegion((mapView?.regionThatFits(viewRegion))!, animated: true)
        self.mapView!.showsUserLocation = true
    }
    
    //MARK: - Extenal Annotation Data Call
    
    private func updateAnnotations(locData: NSArray) { // Called async when update annotations
        for i in 0...locData.count-1 {
            let loc = locData[i] as! NSDictionary
            let newAnnotation = MKPointAnnotation()
            newAnnotation.title = loc["hub"] as? String
            newAnnotation.coordinate = CLLocationCoordinate2D(latitude: Double(loc["lat"] as! String)!, longitude: Double(loc["lng"] as! String)!)
            DispatchQueue.main.async {
                let annotationView = MKPinAnnotationView(annotation: newAnnotation, reuseIdentifier: "BikeLocation")
                annotationView.canShowCallout = true
                self.mapView!.addAnnotation(annotationView.annotation!)
            }
        }
    }
    
    private func getAnnotations() { // Set bike stand locations as markouts
        let locationsURL: String = "http://www.matthewfrankland.co.uk/pedalPay/userFunctions/locations.php"
        do {
            let password = try Keychain(service:Bundle.main.object(forInfoDictionaryKey: "KaychainGroup") as! String).synchronizable(true).get((((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String))
            sendRequest(locationsURL, method: .post, parameters: ["email": (((UserDefaults.standard.value(forKey: "userDetails") as! [[String : Any]])[0])["email"] as! String), "password": password!], completionHandler: { (data, response, error) in
                guard let data = data else { return }
                do {
                    let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String : Any]
                    let locations = json["message"] as! NSArray
                    UserDefaults.standard.set(locations, forKey: "locations")
                    self.updateAnnotations(locData: locations)
                } catch let error as NSError {
                    self.alert(title: "Fatal Error In Annotation Markup", message: error.description, style: UIAlertController.Style.alert)
                }
            })?.resume()
        } catch {
            fatalError("Error fetching password items - \(error)")
        }
    }
        
}
