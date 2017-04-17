//
//  MapsViewController.h
//  KotlinMap
//
//  Created by Admin on 16.04.17.
//  Copyright © 2017 github.devjn. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <GoogleMaps/GoogleMaps.h>

@interface MapsViewController : UIViewController
@property (weak, nonatomic) IBOutlet GMSMapView *mapView;
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;

@end
