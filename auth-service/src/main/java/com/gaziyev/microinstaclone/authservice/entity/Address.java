package com.gaziyev.microinstaclone.authservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Address {

	private String id;
	private String country;
	private String city;
	private String zipCode;
	private String streetName;
	private int buildingNumber;
}
