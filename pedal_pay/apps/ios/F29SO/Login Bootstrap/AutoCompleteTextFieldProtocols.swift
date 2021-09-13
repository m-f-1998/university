//
//  AutoCompleteTextFieldProtocols.swift
//  Pods
//
//  Created by Neil Francis Hipona on 16/07/2016.
//  Copyright (c) 2016 Neil Francis Ramirez Hipona. All rights reserved.
//
import Foundation
import UIKit


// MARK: - AutoCompleteTextField Protocol
public protocol AutoCompleteTextFieldDataSource: NSObjectProtocol {
    
    // Required protocols
    
    func autoCompleteTextFieldDataSource(_ autoCompleteTextField: AutoCompleteTextField) -> [String] // called when in need of suggestions.
}

protocol AutoCompleteTextFieldDelegate: UITextFieldDelegate {
    
    // Optional protocols
    
    // return NO to disallow editing. Defaults to YES.
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool
    
    // became first responder
    func textFieldDidBeginEditing(_ textField: UITextField)
    
    // return YES to allow editing to stop and to resign first responder status. NO to disallow the editing session to end. Defaults to YES.
    func textFieldShouldEndEditing(_ textField: UITextField) -> Bool
    
    // may be called if forced even if shouldEndEditing returns NO (e.g. view removed from window) or endEditing:YES called
    func textFieldDidEndEditing(_ textField: UITextField)
    
    // return NO to not change text. Defaults to YES.
    func textField(_ textField: UITextField, changeCharactersInRange range: NSRange, replacementString string: String) -> Bool
    
    // called when clear button pressed. return NO to ignore (no notifications)
    func textFieldShouldClear(_ textField: UITextField) -> Bool
    
    // called when 'return' key pressed. return NO to ignore.
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    
}

// MARK: - UITextFieldDelegate
extension AutoCompleteTextField: UITextFieldDelegate {
    
    // MARK: - UITextFieldDelegate
    
    public func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        guard let delegate = autoCompleteTextFieldDelegate else { return true }
        let delegateCall = delegate.textFieldShouldBeginEditing
        return delegateCall(self)
    }
    
    public func textFieldDidBeginEditing(_ textField: UITextField) {
        guard let delegate = autoCompleteTextFieldDelegate else { return }
        let delegateCall = delegate.textFieldDidBeginEditing
        delegateCall(self)
    }
    
    public func textFieldShouldEndEditing(_ textField: UITextField) -> Bool {
        guard let delegate = autoCompleteTextFieldDelegate else { return true }
        let delegateCall = delegate.textFieldShouldEndEditing
        return delegateCall(self)
    }
    
    public func textFieldDidEndEditing(_ textField: UITextField) {
        guard let delegate = autoCompleteTextFieldDelegate else { return }
        let delegateCall = delegate.textFieldDidEndEditing
        delegateCall(self)
    }
    
    public func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        guard let delegate = autoCompleteTextFieldDelegate else { return true }
        let delegateCall = delegate.textField(_:changeCharactersInRange:replacementString:)
        return delegateCall(textField, range, string)
    }
    
    public func textFieldShouldClear(_ textField: UITextField) -> Bool {
        guard let delegate = autoCompleteTextFieldDelegate else { return true }
        let delegateCall = delegate.textFieldShouldClear
        return delegateCall(self)
    }
    
    public func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        guard let delegate = autoCompleteTextFieldDelegate else { return endEditing(true) }
        let delegateCall = delegate.textFieldShouldReturn
        return delegateCall(self)
    }
}
